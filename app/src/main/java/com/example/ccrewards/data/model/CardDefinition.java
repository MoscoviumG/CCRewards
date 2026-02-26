package com.example.ccrewards.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "card_definitions")
public class CardDefinition {

    @PrimaryKey
    @NonNull
    public String id;

    public String displayName;
    public String issuer;
    public String network;
    public int annualFee;
    public boolean isCustom;
    public boolean isBusinessCard;
    public long cardColorPrimary;   // ARGB packed as long
    public long cardColorSecondary;
    public String rewardCurrencyName;

    public CardDefinition(@NonNull String id, String displayName, String issuer, String network,
                          int annualFee, boolean isCustom, boolean isBusinessCard,
                          long cardColorPrimary, long cardColorSecondary, String rewardCurrencyName) {
        this.id = id;
        this.displayName = displayName;
        this.issuer = issuer;
        this.network = network;
        this.annualFee = annualFee;
        this.isCustom = isCustom;
        this.isBusinessCard = isBusinessCard;
        this.cardColorPrimary = cardColorPrimary;
        this.cardColorSecondary = cardColorSecondary;
        this.rewardCurrencyName = rewardCurrencyName;
    }
}
