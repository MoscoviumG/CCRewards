package com.example.ccrewards.data.model.relations;

import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.UserCard;

/** Flat projection used in the Credits Overview screen. */
public class BenefitWithUsage {
    public UserCard userCard;
    public CardDefinition definition;
    public CardBenefit benefit;
    public BenefitUsage usage;  // null = never tracked in current period

    public BenefitWithUsage(UserCard userCard, CardDefinition definition,
                            CardBenefit benefit, BenefitUsage usage) {
        this.userCard = userCard;
        this.definition = definition;
        this.benefit = benefit;
        this.usage = usage;
    }

    public boolean isUsed() {
        return usage != null && usage.isUsed;
    }
}
