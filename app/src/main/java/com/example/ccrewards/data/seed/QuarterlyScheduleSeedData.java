package com.example.ccrewards.data.seed;

import com.example.ccrewards.data.model.QuarterlyBonusScheduleRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Hardcoded quarterly rotating bonus schedules for known cards.
 *
 * <p>To add a new year/quarter when categories are announced:
 * <ol>
 *   <li>Add rows below</li>
 *   <li>Bump {@code AppDatabase.SEED_VERSION}</li>
 * </ol>
 * No DB migration needed — seed refresh uses upsertAll (INSERT OR REPLACE).
 */
public class QuarterlyScheduleSeedData {

    public static List<QuarterlyBonusScheduleRow> getSchedule() {
        List<QuarterlyBonusScheduleRow> rows = new ArrayList<>();

        // ── Chase Freedom Flex ────────────────────────────────────────────────
        // 2025 Q1: Grocery stores, select streaming services (streaming omitted — no enum match)
        add(rows, "chase_freedom_flex", 2025, 1, "GROCERIES", 5.0, "CASHBACK", 150000);
        add(rows, "chase_freedom_flex", 2025, 1, "ENTERTAINMENT", 5.0, "CASHBACK", 150000);

        // 2025 Q2: Amazon, hotels (online_shopping best match; hotels not a separate enum)
        add(rows, "chase_freedom_flex", 2025, 2, "ONLINE_SHOPPING", 5.0, "CASHBACK", 150000);

        // 2025 Q3: Gas stations, select live entertainment
        add(rows, "chase_freedom_flex", 2025, 3, "GAS", 5.0, "CASHBACK", 150000);

        // 2025 Q4: PayPal, select charities — best mapped to GENERAL (no dedicated enum)
        add(rows, "chase_freedom_flex", 2025, 4, "GENERAL", 5.0, "CASHBACK", 150000);

        // 2026 Q1: Grocery stores, select streaming (streaming omitted)
        add(rows, "chase_freedom_flex", 2026, 1, "GROCERIES", 5.0, "CASHBACK", 150000);

        // ── Discover it Cash Back ─────────────────────────────────────────────
        // 2025 Q1: Grocery stores, fitness clubs & gym memberships (gym omitted)
        add(rows, "discover_it_cashback", 2025, 1, "GROCERIES", 5.0, "CASHBACK", 150000);

        // 2025 Q2: Gas stations, home improvement stores, public transit
        add(rows, "discover_it_cashback", 2025, 2, "GAS", 5.0, "CASHBACK", 150000);

        // 2025 Q3: Restaurants, drug stores
        add(rows, "discover_it_cashback", 2025, 3, "DINING", 5.0, "CASHBACK", 150000);
        add(rows, "discover_it_cashback", 2025, 3, "ONLINE_SHOPPING", 5.0, "CASHBACK", 150000);

        // 2025 Q4: Amazon, digital wallets (digital wallets omitted — no enum match)
        add(rows, "discover_it_cashback", 2025, 4, "ONLINE_SHOPPING", 5.0, "CASHBACK", 150000);

        // 2026 Q1: Grocery stores, rideshare
        add(rows, "discover_it_cashback", 2026, 1, "GROCERIES", 5.0, "CASHBACK", 150000);

        return rows;
    }

    private static void add(List<QuarterlyBonusScheduleRow> rows,
                             String cardId, int year, int quarter,
                             String categoryName, double rate, String rateType,
                             int spendLimitCents) {
        QuarterlyBonusScheduleRow row = new QuarterlyBonusScheduleRow();
        row.cardDefinitionId = cardId;
        row.year = year;
        row.quarter = quarter;
        row.categoryName = categoryName;
        row.rate = rate;
        row.rateType = rateType;
        row.spendLimitCents = spendLimitCents;
        rows.add(row);
    }
}
