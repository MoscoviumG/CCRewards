package com.example.ccrewards.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rotational_bonus_categories")
public class RotationalBonusCategory {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long rotationalBonusId;

    /** Either a {@link RewardCategory#name()} string for built-in categories,
     *  or free-text for custom spend categories (e.g. "Amazon", "Cruise Fare"). */
    public String categoryName;

    public double rate;

    public RateType rateType;

    /** Optional currency override; null = use card default. */
    public String currencyName;
}
