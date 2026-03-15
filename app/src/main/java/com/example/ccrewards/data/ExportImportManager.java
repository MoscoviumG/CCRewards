package com.example.ccrewards.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.ccrewards.data.db.AppDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class ExportImportManager {

    private static final int EXPORT_VERSION = 1;

    private final AppDatabase db;
    private final ContentResolver contentResolver;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public interface Callback {
        void onSuccess(String msg);
        void onError(String msg);
    }

    @Inject
    public ExportImportManager(AppDatabase db, @ApplicationContext Context context) {
        this.db = db;
        this.contentResolver = context.getContentResolver();
    }

    // ─── Export ───────────────────────────────────────────────────────────────

    public void exportToUri(Uri uri, Callback cb) {
        executor.execute(() -> {
            try {
                JSONObject root = new JSONObject();
                root.put("exportVersion", EXPORT_VERSION);
                root.put("dbVersion", 19);
                root.put("exportedAt", java.time.LocalDate.now().toString());

                SupportSQLiteDatabase raw = db.getOpenHelper().getReadableDatabase();

                // Full user data tables
                root.put("user_cards",                   tableToJsonArray(raw, "SELECT * FROM user_cards"));
                root.put("benefit_usage",                tableToJsonArray(raw, "SELECT * FROM benefit_usage"));
                root.put("product_change_records",       tableToJsonArray(raw, "SELECT * FROM product_change_records"));
                root.put("user_card_choice_categories",  tableToJsonArray(raw, "SELECT * FROM user_card_choice_categories"));
                root.put("welcome_bonuses",              tableToJsonArray(raw, "SELECT * FROM welcome_bonuses"));
                root.put("rotational_bonuses",           tableToJsonArray(raw, "SELECT * FROM rotational_bonuses"));
                root.put("rotational_bonus_categories",  tableToJsonArray(raw, "SELECT * FROM rotational_bonus_categories"));
                root.put("custom_categories",            tableToJsonArray(raw, "SELECT * FROM custom_categories"));
                root.put("custom_category_rates",        tableToJsonArray(raw, "SELECT * FROM custom_category_rates"));
                root.put("free_night_awards",            tableToJsonArray(raw, "SELECT * FROM free_night_awards"));
                root.put("starred_benefits",             tableToJsonArray(raw, "SELECT * FROM starred_benefits"));
                root.put("auto_bonus_created",           tableToJsonArray(raw, "SELECT * FROM auto_bonus_created"));

                // Selective seed customizations
                root.put("custom_card_definitions", tableToJsonArray(raw, "SELECT * FROM card_definitions WHERE isCustom = 1"));
                root.put("custom_card_benefits",    tableToJsonArray(raw, "SELECT * FROM card_benefits WHERE isCustom = 1"));
                root.put("customized_reward_rates", tableToJsonArray(raw, "SELECT * FROM reward_rates WHERE isCustomized = 1"));
                root.put("point_valuations",        tableToJsonArray(raw, "SELECT * FROM point_valuations"));
                root.put("free_night_valuations",   tableToJsonArray(raw, "SELECT * FROM free_night_valuations"));

                String json = root.toString(2);

                try (OutputStreamWriter writer = new OutputStreamWriter(
                        contentResolver.openOutputStream(uri))) {
                    writer.write(json);
                }

                cb.onSuccess("Export complete.");
            } catch (Exception e) {
                cb.onError("Export failed: " + e.getMessage());
            }
        });
    }

    /** Reads all rows from the given SQL query into a JSONArray. */
    private JSONArray tableToJsonArray(SupportSQLiteDatabase raw, String sql) throws JSONException {
        JSONArray array = new JSONArray();
        try (Cursor cursor = raw.query(sql)) {
            String[] cols = cursor.getColumnNames();
            while (cursor.moveToNext()) {
                JSONObject row = new JSONObject();
                for (int i = 0; i < cols.length; i++) {
                    if (cursor.isNull(i)) {
                        row.put(cols[i], JSONObject.NULL);
                    } else {
                        switch (cursor.getType(i)) {
                            case Cursor.FIELD_TYPE_INTEGER:
                                row.put(cols[i], cursor.getLong(i));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                row.put(cols[i], cursor.getDouble(i));
                                break;
                            default:
                                row.put(cols[i], cursor.getString(i));
                                break;
                        }
                    }
                }
                array.put(row);
            }
        }
        return array;
    }

    // ─── Import ───────────────────────────────────────────────────────────────

    public void importFromUri(Uri uri, Callback cb) {
        executor.execute(() -> {
            try {
                // Read JSON from Uri
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(contentResolver.openInputStream(uri)))) {
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line).append('\n');
                }

                JSONObject root = new JSONObject(sb.toString());

                SupportSQLiteDatabase raw = db.getOpenHelper().getWritableDatabase();

                raw.execSQL("PRAGMA foreign_keys = OFF");
                raw.beginTransaction();
                try {
                    deleteAllUserData(raw);

                    // Custom categories before their rates (FK)
                    insertJsonArray(raw, "custom_categories",       root.getJSONArray("custom_categories"),      false);
                    insertJsonArray(raw, "custom_category_rates",   root.getJSONArray("custom_category_rates"),  false);

                    // Custom card definitions before custom benefits (FK)
                    insertJsonArray(raw, "card_definitions",        root.getJSONArray("custom_card_definitions"), true);
                    insertJsonArray(raw, "card_benefits",           root.getJSONArray("custom_card_benefits"),    true);

                    // Customized reward rates (upsert)
                    insertJsonArray(raw, "reward_rates",            root.getJSONArray("customized_reward_rates"), true);

                    // Point valuations — update only, rows already exist from seed
                    JSONArray pvRows = root.getJSONArray("point_valuations");
                    for (int i = 0; i < pvRows.length(); i++) {
                        JSONObject row = pvRows.getJSONObject(i);
                        raw.execSQL(
                                "UPDATE point_valuations SET centsPerPoint = ? WHERE rewardCurrencyName = ?",
                                new Object[]{ row.getDouble("centsPerPoint"), row.getString("rewardCurrencyName") });
                    }

                    // Free night valuations — update only
                    JSONArray fnvRows = root.getJSONArray("free_night_valuations");
                    for (int i = 0; i < fnvRows.length(); i++) {
                        JSONObject row = fnvRows.getJSONObject(i);
                        raw.execSQL(
                                "UPDATE free_night_valuations SET valueCents = ? WHERE typeKey = ?",
                                new Object[]{ row.getInt("valueCents"), row.getString("typeKey") });
                    }

                    // User data (FK parent before child)
                    insertJsonArray(raw, "user_cards",                  root.getJSONArray("user_cards"),                 false);
                    insertJsonArray(raw, "welcome_bonuses",             root.getJSONArray("welcome_bonuses"),            false);
                    insertJsonArray(raw, "rotational_bonuses",          root.getJSONArray("rotational_bonuses"),         false);
                    insertJsonArray(raw, "rotational_bonus_categories", root.getJSONArray("rotational_bonus_categories"), false);
                    insertJsonArray(raw, "benefit_usage",               root.getJSONArray("benefit_usage"),              false);
                    insertJsonArray(raw, "product_change_records",      root.getJSONArray("product_change_records"),     false);
                    insertJsonArray(raw, "user_card_choice_categories", root.getJSONArray("user_card_choice_categories"), false);
                    insertJsonArray(raw, "free_night_awards",           root.getJSONArray("free_night_awards"),          false);
                    insertJsonArray(raw, "starred_benefits",            root.getJSONArray("starred_benefits"),           false);
                    insertJsonArray(raw, "auto_bonus_created",          root.getJSONArray("auto_bonus_created"),         false);

                    raw.setTransactionSuccessful();
                } finally {
                    raw.endTransaction();
                    raw.execSQL("PRAGMA foreign_keys = ON");
                }

                cb.onSuccess("Import complete. Restarting app…");
            } catch (Exception e) {
                cb.onError("Import failed: " + e.getMessage());
            }
        });
    }

    private void deleteAllUserData(SupportSQLiteDatabase raw) {
        raw.execSQL("DELETE FROM benefit_usage");
        raw.execSQL("DELETE FROM product_change_records");
        raw.execSQL("DELETE FROM user_card_choice_categories");
        raw.execSQL("DELETE FROM welcome_bonuses");
        raw.execSQL("DELETE FROM rotational_bonus_categories");
        raw.execSQL("DELETE FROM rotational_bonuses");
        raw.execSQL("DELETE FROM free_night_awards");
        raw.execSQL("DELETE FROM starred_benefits");
        raw.execSQL("DELETE FROM auto_bonus_created");
        raw.execSQL("DELETE FROM user_cards");
        raw.execSQL("DELETE FROM custom_category_rates");
        raw.execSQL("DELETE FROM custom_categories");
        raw.execSQL("DELETE FROM card_benefits WHERE isCustom = 1");
        raw.execSQL("DELETE FROM reward_rates WHERE isCustomized = 1");
        raw.execSQL("DELETE FROM card_definitions WHERE isCustom = 1");
    }

    /**
     * INSERT OR REPLACE (orReplace=true) or INSERT (orReplace=false) all rows from the array
     * into the given table. Builds the statement once and loops.
     */
    private void insertJsonArray(SupportSQLiteDatabase raw, String table, JSONArray rows, boolean orReplace)
            throws JSONException {
        if (rows.length() == 0) return;

        JSONObject first = rows.getJSONObject(0);
        java.util.Iterator<String> keyIt = first.keys();
        java.util.List<String> colList = new java.util.ArrayList<>();
        while (keyIt.hasNext()) colList.add(keyIt.next());
        String[] cols = colList.toArray(new String[0]);
        if (cols.length == 0) return;

        StringBuilder sql = new StringBuilder();
        sql.append(orReplace ? "INSERT OR REPLACE INTO `" : "INSERT INTO `");
        sql.append(table).append("` (");
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sql.append(", ");
            sql.append('`').append(cols[i]).append('`');
        }
        sql.append(") VALUES (");
        for (int i = 0; i < cols.length; i++) {
            if (i > 0) sql.append(", ");
            sql.append('?');
        }
        sql.append(')');

        Object[] bindings = new Object[cols.length];
        for (int r = 0; r < rows.length(); r++) {
            JSONObject row = rows.getJSONObject(r);
            for (int i = 0; i < cols.length; i++) {
                Object val = row.opt(cols[i]);
                if (val == null || val == JSONObject.NULL) {
                    bindings[i] = null;
                } else {
                    bindings[i] = val;
                }
            }
            raw.execSQL(sql.toString(), bindings);
        }
    }
}
