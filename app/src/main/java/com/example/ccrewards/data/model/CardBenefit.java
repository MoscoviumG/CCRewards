package com.example.ccrewards.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "card_benefits")
public class CardBenefit {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String cardDefinitionId;
    public String name;
    public String description;
    public int amountCents;
    public ResetPeriod resetPeriod;
    public boolean isCustom;
    @NonNull
    public ResetType resetType;

    /** Room constructor — used for deserialization. */
    public CardBenefit(String cardDefinitionId, String name, String description,
                       int amountCents, ResetPeriod resetPeriod, boolean isCustom, @NonNull ResetType resetType) {
        this.cardDefinitionId = cardDefinitionId;
        this.name = name;
        this.description = description;
        this.amountCents = amountCents;
        this.resetPeriod = resetPeriod;
        this.isCustom = isCustom;
        this.resetType = resetType;
    }

    /** Convenience constructor — defaults resetType to CALENDAR. */
    @Ignore
    public CardBenefit(String cardDefinitionId, String name, String description,
                       int amountCents, ResetPeriod resetPeriod, boolean isCustom) {
        this(cardDefinitionId, name, description, amountCents, resetPeriod, isCustom, ResetType.CALENDAR);
    }
}
