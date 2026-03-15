package com.example.ccrewards.data.model;

import androidx.room.Entity;

@Entity(tableName = "starred_benefits",
        primaryKeys = {"userCardId", "benefitId"})
public class StarredBenefit {
    public long userCardId;
    public long benefitId;

    public StarredBenefit(long userCardId, long benefitId) {
        this.userCardId = userCardId;
        this.benefitId = benefitId;
    }
}
