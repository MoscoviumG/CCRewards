package com.example.ccrewards.data.model;

import androidx.room.ColumnInfo;

/** POJO returned by BenefitUsageDao.getAllUsedWithAmounts() — a join of benefit_usage + card_benefits. */
public class BenefitUsageWithAmount {
    @ColumnInfo(name = "userCardId") public long userCardId;
    @ColumnInfo(name = "periodKey") public String periodKey;
    @ColumnInfo(name = "amountCents") public int amountCents;
}
