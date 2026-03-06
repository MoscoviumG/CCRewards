package com.example.ccrewards.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "rotational_bonuses")
public class RotationalBonus {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userCardId;

    public String label;          // e.g. "Q2 2025 Bonus"

    @ColumnInfo(defaultValue = "150000")
    public int spendLimitCents;   // 0 = no limit

    @ColumnInfo(defaultValue = "0")
    public int usedCents;

    public LocalDate endDate;     // null = never expires

    @ColumnInfo(defaultValue = "0")
    public boolean isFullyUsed;
}
