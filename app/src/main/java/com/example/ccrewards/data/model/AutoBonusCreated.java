package com.example.ccrewards.data.model;

import androidx.room.Entity;

/**
 * Deduplication tracker: records that an auto-bonus was already created for a
 * given (userCard, year, quarter) tuple so it won't be re-created on subsequent
 * app opens — even if the user deleted it.
 */
@Entity(tableName = "auto_bonus_created",
        primaryKeys = {"userCardId", "year", "quarter"})
public class AutoBonusCreated {

    public long userCardId;
    public int year;
    public int quarter;

    public AutoBonusCreated(long userCardId, int year, int quarter) {
        this.userCardId = userCardId;
        this.year = year;
        this.quarter = quarter;
    }
}
