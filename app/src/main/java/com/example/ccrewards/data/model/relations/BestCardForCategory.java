package com.example.ccrewards.data.model.relations;

import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.RewardCategory;

public class BestCardForCategory {

    public RewardCategory category;
    public String cardDefinitionId;
    public String cardDisplayName;
    public String rewardCurrencyName;
    public RateType rateType;
    public double rate;
    public double effectiveReturn;  // rate × centsPerPoint / 100, computed in ViewModel
    public boolean isUserOwned;

    public BestCardForCategory(RewardCategory category, String cardDefinitionId,
                               String cardDisplayName, String rewardCurrencyName,
                               RateType rateType, double rate,
                               double effectiveReturn, boolean isUserOwned) {
        this.category = category;
        this.cardDefinitionId = cardDefinitionId;
        this.cardDisplayName = cardDisplayName;
        this.rewardCurrencyName = rewardCurrencyName;
        this.rateType = rateType;
        this.rate = rate;
        this.effectiveReturn = effectiveReturn;
        this.isUserOwned = isUserOwned;
    }
}
