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
        return new ArrayList<>();
    }
}
