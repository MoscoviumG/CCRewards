package com.example.ccrewards.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "reward_rates")
public class RewardRate {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String cardDefinitionId;
    public RewardCategory category;
    public RateType rateType;
    public String currencyName;     // user-selected currency override; null = use card default
    public double rate;
    public boolean isChoiceCategory;
    public String choiceGroupId;    // null if not a choice category
    public boolean isCustomized;

    public RewardRate(String cardDefinitionId, RewardCategory category,
                      RateType rateType, double rate,
                      boolean isChoiceCategory, String choiceGroupId,
                      boolean isCustomized) {
        this.cardDefinitionId = cardDefinitionId;
        this.category = category;
        this.rateType = rateType;
        this.rate = rate;
        this.isChoiceCategory = isChoiceCategory;
        this.choiceGroupId = choiceGroupId;
        this.isCustomized = isCustomized;
    }

    /** Convenience constructor for non-choice, non-customized rates. */
    @Ignore
    public RewardRate(String cardDefinitionId, RewardCategory category,
                      RateType rateType, double rate) {
        this(cardDefinitionId, category, rateType, rate, false, null, false);
    }
}
