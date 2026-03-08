package com.example.ccrewards.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ccrewards.data.model.RewardCategory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Persists the user's chosen order and visibility for Best Card category tiles.
 *
 * Key format:
 *   Built-in category  →  "B:DINING"   (RewardCategory enum name)
 *   Custom category    →  "C:42"       (database id as string)
 */
public class CategoryDisplayPrefs {

    private static final String PREFS_NAME = "category_display_prefs";
    private static final String KEY_ORDER  = "category_order";
    private static final String KEY_HIDDEN = "category_hidden";

    // Default built-in categories shown in the Best Card grid
    public static final RewardCategory[] DEFAULT_BUILTIN = {
            RewardCategory.GENERAL,
            RewardCategory.DINING,
            RewardCategory.GROCERIES,
            RewardCategory.TRAVEL,
            RewardCategory.GAS,
            RewardCategory.ENTERTAINMENT,
            RewardCategory.ONLINE_SHOPPING,
            RewardCategory.RENT_MORTGAGE
    };

    // ── Key helpers ──────────────────────────────────────────────────────────

    public static String builtinKey(RewardCategory cat) {
        return "B:" + cat.name();
    }

    public static String customKey(long id) {
        return "C:" + id;
    }

    public static boolean isCustomKey(String key) {
        return key != null && key.startsWith("C:");
    }

    public static long customIdFromKey(String key) {
        return Long.parseLong(key.substring(2));
    }

    public static RewardCategory builtinCategoryFromKey(String key) {
        return RewardCategory.valueOf(key.substring(2));
    }

    public static String labelForBuiltin(RewardCategory cat) {
        switch (cat) {
            case GENERAL:          return "General";
            case DINING:           return "Dining";
            case GROCERIES:        return "Groceries";
            case TRAVEL:           return "Travel";
            case GAS:              return "Gas";
            case ENTERTAINMENT:    return "Entertainment";
            case ONLINE_SHOPPING:  return "Online Shopping";
            case RENT_MORTGAGE:    return "Rent / Mortgage";
            default:               return cat.name();
        }
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    /** Raw ordered key list from prefs. Empty list = not yet initialised. */
    public static List<String> getOrderedKeys(Context ctx) {
        String saved = prefs(ctx).getString(KEY_ORDER, "");
        if (saved.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(saved.split(",")));
    }

    /**
     * Returns the ordered key list merged with the live custom-category ids:
     * — new custom ids are appended (visible by default)
     * — deleted custom ids are removed
     * — if never saved before, default built-in order is used
     */
    public static List<String> getMergedOrderedKeys(Context ctx, List<Long> existingCustomIds) {
        List<String> saved = getOrderedKeys(ctx);

        if (saved.isEmpty()) {
            List<String> result = new ArrayList<>();
            for (RewardCategory cat : DEFAULT_BUILTIN) result.add(builtinKey(cat));
            for (long id : existingCustomIds) result.add(customKey(id));
            saveOrder(ctx, result);
            return result;
        }

        // Build valid custom key set
        Set<String> validCustomKeys = new HashSet<>();
        for (long id : existingCustomIds) validCustomKeys.add(customKey(id));

        // Remove deleted custom categories
        List<String> result = new ArrayList<>();
        for (String key : saved) {
            if (isCustomKey(key)) {
                if (validCustomKeys.contains(key)) result.add(key);
            } else {
                result.add(key);
            }
        }

        // Append newly added custom categories
        for (long id : existingCustomIds) {
            String key = customKey(id);
            if (!result.contains(key)) result.add(key);
        }

        return result;
    }

    public static Set<String> getHiddenKeys(Context ctx) {
        String saved = prefs(ctx).getString(KEY_HIDDEN, "");
        if (saved.isEmpty()) return new HashSet<>();
        return new HashSet<>(Arrays.asList(saved.split(",")));
    }

    public static boolean isVisible(Context ctx, String key) {
        return !getHiddenKeys(ctx).contains(key);
    }

    // ── Write ────────────────────────────────────────────────────────────────

    public static void saveOrder(Context ctx, List<String> orderedKeys) {
        prefs(ctx).edit().putString(KEY_ORDER, String.join(",", orderedKeys)).apply();
    }

    public static void setVisible(Context ctx, String key, boolean visible) {
        Set<String> hidden = getHiddenKeys(ctx);
        if (visible) hidden.remove(key); else hidden.add(key);
        prefs(ctx).edit().putString(KEY_HIDDEN, String.join(",", hidden)).apply();
    }

    // ── Private ──────────────────────────────────────────────────────────────

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
