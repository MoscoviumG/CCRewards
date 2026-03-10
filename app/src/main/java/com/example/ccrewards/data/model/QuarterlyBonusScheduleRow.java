package com.example.ccrewards.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** One category row in the hardcoded quarterly 5% rotating bonus schedule. */
@Entity(tableName = "quarterly_bonus_schedule")
public class QuarterlyBonusScheduleRow {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String cardDefinitionId;  // e.g. "chase_freedom_flex"
    public int year;                 // e.g. 2026
    public int quarter;              // 1–4

    /** RewardCategory enum name (e.g. "GROCERIES"). */
    public String categoryName;

    /** Full bonus rate (e.g. 5.0 for 5%). */
    public double rate;

    /** RateType enum name (e.g. "CASHBACK"). */
    public String rateType;

    /** Quarter spend cap in cents (typically 150000 = $1500). */
    public int spendLimitCents;
}
