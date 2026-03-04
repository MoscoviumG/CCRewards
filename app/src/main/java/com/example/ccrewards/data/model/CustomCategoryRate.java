package com.example.ccrewards.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "custom_category_rates")
public class CustomCategoryRate {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long customCategoryId;
    @NonNull
    public String cardDefinitionId;
    public double rate;
    @NonNull
    public RateType rateType;
    @NonNull
    public String currencyName = "";

    public CustomCategoryRate() {
        cardDefinitionId = "";
        rateType = RateType.POINTS;
    }

    @Ignore
    public CustomCategoryRate(long customCategoryId, @NonNull String cardDefinitionId,
                               double rate, @NonNull RateType rateType) {
        this.customCategoryId = customCategoryId;
        this.cardDefinitionId = cardDefinitionId;
        this.rate = rate;
        this.rateType = rateType;
    }

    @Ignore
    public CustomCategoryRate(long customCategoryId, @NonNull String cardDefinitionId,
                               double rate, @NonNull RateType rateType,
                               @NonNull String currencyName) {
        this.customCategoryId = customCategoryId;
        this.cardDefinitionId = cardDefinitionId;
        this.rate = rate;
        this.rateType = rateType;
        this.currencyName = currencyName;
    }
}
