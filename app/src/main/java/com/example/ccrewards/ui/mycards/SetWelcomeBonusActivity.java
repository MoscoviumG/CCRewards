package com.example.ccrewards.ui.mycards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ccrewards.databinding.ActivitySetWelcomeBonusBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Full-screen activity for adding or editing a welcome bonus.
 * Callers use ActivityResultLauncher; results are returned via Intent extras.
 *
 * Input extras (all optional — omit for a blank "add" form):
 *   EXTRA_CURRENCY   String   reward currency name
 *   EXTRA_BONUS      int      existing bonusPoints
 *   EXTRA_SPEND      int      existing spendRequirementCents
 *   EXTRA_DEADLINE   long     epoch day (-1 = none)
 *   EXTRA_SHOW_BC    boolean  showInBestCard
 *   EXTRA_CASHBACK   int      cashbackCents
 *   EXTRA_FN_TYPE    String   fnTypeKey (null = none)
 *   EXTRA_FN_COUNT   int      fnCount
 *
 * Output extras (on RESULT_OK):
 *   Same keys as above, always present.
 */
public class SetWelcomeBonusActivity extends AppCompatActivity {

    public static final String EXTRA_CURRENCY  = "currency";
    public static final String EXTRA_BONUS     = "bonus_pts";
    public static final String EXTRA_SPEND     = "spend_req";
    public static final String EXTRA_DEADLINE  = "deadline_epoch";
    public static final String EXTRA_SHOW_BC   = "show_bc";
    public static final String EXTRA_CASHBACK  = "cashback_cents";
    public static final String EXTRA_FN_TYPE   = "fn_type_key";
    public static final String EXTRA_FN_COUNT  = "fn_count";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);

    private static final String[] FN_TYPE_KEYS = {
        "HILTON_UNLIMITED", "MARRIOTT_35000", "MARRIOTT_85000",
        "IHG_40000", "IHG_60000", "IHG_100000",
        "HYATT_CAT_1", "HYATT_CAT_2", "HYATT_CAT_3", "HYATT_CAT_4",
        "HYATT_CAT_5", "HYATT_CAT_6", "HYATT_CAT_7",
        "WYNDHAM_30000",
    };

    private static final String[] FN_DISPLAY_NAMES = {
        "Hilton – Unlimited", "Marriott – 35k", "Marriott – 85k",
        "IHG – 40k", "IHG – 60k", "IHG – 100k",
        "Hyatt – Category 1", "Hyatt – Category 2", "Hyatt – Category 3",
        "Hyatt – Category 4", "Hyatt – Category 5", "Hyatt – Category 6",
        "Hyatt – Category 7",
        "Wyndham – 30k",
    };

    private ActivitySetWelcomeBonusBinding binding;
    private String currencyName;
    private LocalDate selectedDeadline;
    private String pendingFnTypeKey;
    private int pendingFnCount = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetWelcomeBonusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        Intent in = getIntent();
        currencyName = in.getStringExtra(EXTRA_CURRENCY);
        if (currencyName == null) currencyName = "";

        int existingBonus    = in.getIntExtra(EXTRA_BONUS, 0);
        int existingSpend    = in.getIntExtra(EXTRA_SPEND, 0);
        long deadlineEpoch   = in.getLongExtra(EXTRA_DEADLINE, -1L);
        boolean showBestCard = in.getBooleanExtra(EXTRA_SHOW_BC, true);
        int existingCashback = in.getIntExtra(EXTRA_CASHBACK, 0);
        pendingFnTypeKey     = in.getStringExtra(EXTRA_FN_TYPE);
        pendingFnCount       = in.getIntExtra(EXTRA_FN_COUNT, 1);

        // Configure bonus field
        boolean isCash = isCashBack(currencyName);
        if (isCash) {
            binding.tilWbBonus.setHint("Bonus amount ($)");
            binding.tvWbCurrencyLabel.setVisibility(android.view.View.GONE);
            if (existingBonus > 0)
                binding.etWbBonus.setText(String.format(Locale.US, "%.2f", existingBonus / 100.0));
        } else {
            binding.tilWbBonus.setHint("Bonus points (e.g. 60,000)");
            binding.tvWbCurrencyLabel.setVisibility(android.view.View.VISIBLE);
            binding.tvWbCurrencyLabel.setText(currencyName);
            if (existingBonus > 0)
                binding.etWbBonus.setText(String.valueOf(existingBonus));
        }

        // Cashback field — hidden for pure cashback cards
        binding.tilWbCashback.setVisibility(isCash ? android.view.View.GONE : android.view.View.VISIBLE);
        if (!isCash && existingCashback > 0)
            binding.etWbCashback.setText(
                    String.format(Locale.US, "%.2f", existingCashback / 100.0));

        // Spend
        if (existingSpend > 0)
            binding.etWbSpend.setText(
                    String.format(Locale.US, "%.2f", existingSpend / 100.0));

        // Deadline
        if (deadlineEpoch >= 0) {
            selectedDeadline = LocalDate.ofEpochDay(deadlineEpoch);
            binding.btnWbDeadline.setText(selectedDeadline.format(DATE_FMT));
        }

        binding.switchWbShowBestCard.setChecked(showBestCard);

        updateFnSummary();
        binding.btnWbFnEdit.setOnClickListener(v -> showFnPickerDialog());
        binding.btnWbDeadline.setOnClickListener(v -> showDatePicker());
        binding.btnWbSave.setOnClickListener(v -> onSave());
    }

    private void updateFnSummary() {
        if (pendingFnTypeKey == null) {
            binding.tvWbFnSummary.setText("None");
        } else {
            binding.tvWbFnSummary.setText(pendingFnCount + "× " + displayNameForKey(pendingFnTypeKey));
        }
    }

    private void showFnPickerDialog() {
        String[] menuItems = new String[FN_DISPLAY_NAMES.length + 1];
        menuItems[0] = "None (no free nights)";
        System.arraycopy(FN_DISPLAY_NAMES, 0, menuItems, 1, FN_DISPLAY_NAMES.length);

        int checkedItem = 0;
        if (pendingFnTypeKey != null) {
            for (int i = 0; i < FN_TYPE_KEYS.length; i++) {
                if (FN_TYPE_KEYS[i].equals(pendingFnTypeKey)) { checkedItem = i + 1; break; }
            }
        }

        float dp = getResources().getDisplayMetrics().density;
        int pad = (int) (16 * dp);

        android.widget.Spinner spinner = new android.widget.Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, menuItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(checkedItem);

        TextView tvCount = new TextView(this);
        tvCount.setText("How many?");
        tvCount.setPadding(0, (int) (8 * dp), 0, 0);

        NumberPicker np = new NumberPicker(this);
        np.setMinValue(1);
        np.setMaxValue(10);
        np.setValue(pendingFnCount);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(pad, (int) (8 * dp), pad, 0);
        layout.addView(spinner);
        layout.addView(tvCount);
        layout.addView(np);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Free Night Certificates")
                .setView(layout)
                .setPositiveButton("OK", (d, w) -> {
                    int idx = spinner.getSelectedItemPosition();
                    if (idx == 0) {
                        pendingFnTypeKey = null;
                        pendingFnCount = 1;
                    } else {
                        pendingFnTypeKey = FN_TYPE_KEYS[idx - 1];
                        pendingFnCount = np.getValue();
                    }
                    updateFnSummary();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select deadline")
                .setSelection(selectedDeadline != null
                        ? selectedDeadline.toEpochDay() * 86_400_000L
                        : MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        picker.addOnPositiveButtonClickListener(ms -> {
            selectedDeadline = Instant.ofEpochMilli(ms).atZone(ZoneId.of("UTC")).toLocalDate();
            binding.btnWbDeadline.setText(selectedDeadline.format(DATE_FMT));
        });
        picker.addOnNegativeButtonClickListener(v -> {
            selectedDeadline = null;
            binding.btnWbDeadline.setText("No deadline");
        });
        picker.show(getSupportFragmentManager(), "date_picker");
    }

    private void onSave() {
        String bonusStr = binding.etWbBonus.getText() != null
                ? binding.etWbBonus.getText().toString().trim() : "";
        String spendStr = binding.etWbSpend.getText() != null
                ? binding.etWbSpend.getText().toString().trim() : "";

        boolean hasFn = pendingFnTypeKey != null;
        if (bonusStr.isEmpty() && !hasFn) {
            binding.tilWbBonus.setError("Required");
            return;
        }
        if (spendStr.isEmpty()) {
            binding.tilWbSpend.setError("Required");
            return;
        }

        double bonusDouble = 0;
        double spendDouble;
        try {
            if (!bonusStr.isEmpty()) bonusDouble = Double.parseDouble(bonusStr);
            spendDouble = Double.parseDouble(spendStr);
        } catch (NumberFormatException e) { return; }

        if (!bonusStr.isEmpty() && bonusDouble <= 0) {
            binding.tilWbBonus.setError("Must be > 0"); return;
        }
        if (spendDouble <= 0) { binding.tilWbSpend.setError("Must be > 0"); return; }

        int bonusValue = isCashBack(currencyName)
                ? (int) (bonusDouble * 100)
                : (int) bonusDouble;
        int spendCents = (int) (spendDouble * 100);

        String cashStr = binding.etWbCashback.getText() != null
                ? binding.etWbCashback.getText().toString().trim() : "";
        int cashCents = 0;
        if (!cashStr.isEmpty()) {
            try { cashCents = (int) (Double.parseDouble(cashStr) * 100); }
            catch (NumberFormatException ignored) {}
        }

        Intent result = new Intent();
        result.putExtra(EXTRA_CURRENCY, currencyName);
        result.putExtra(EXTRA_BONUS, bonusValue);
        result.putExtra(EXTRA_SPEND, spendCents);
        result.putExtra(EXTRA_DEADLINE, selectedDeadline != null ? selectedDeadline.toEpochDay() : -1L);
        result.putExtra(EXTRA_SHOW_BC, binding.switchWbShowBestCard.isChecked());
        result.putExtra(EXTRA_CASHBACK, cashCents);
        result.putExtra(EXTRA_FN_TYPE, pendingFnTypeKey);
        result.putExtra(EXTRA_FN_COUNT, pendingFnCount);

        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private static String displayNameForKey(String typeKey) {
        for (int i = 0; i < FN_TYPE_KEYS.length; i++) {
            if (FN_TYPE_KEYS[i].equals(typeKey)) return FN_DISPLAY_NAMES[i];
        }
        return typeKey;
    }

    public static boolean isCashBack(String name) {
        return name != null && name.toLowerCase(Locale.US).contains("cash");
    }
}
