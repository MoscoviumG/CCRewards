package com.example.ccrewards.data.model.relations;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.model.UserCard;

import java.util.List;

public class UserCardWithDetails {

    @Embedded
    public UserCard userCard;

    @Relation(parentColumn = "cardDefinitionId", entityColumn = "id")
    public CardDefinition definition;

    @Relation(parentColumn = "cardDefinitionId", entityColumn = "cardDefinitionId",
              entity = RewardRate.class)
    public List<RewardRate> rewardRates;

    @Relation(parentColumn = "cardDefinitionId", entityColumn = "cardDefinitionId",
              entity = CardBenefit.class)
    public List<CardBenefit> benefits;
}
