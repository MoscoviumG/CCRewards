package com.example.ccrewards.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "point_valuations")
public class PointValuation {

    @PrimaryKey
    @NonNull
    public String rewardCurrencyName;

    public double centsPerPoint;
    public double defaultCentsPerPoint;

    public PointValuation(@NonNull String rewardCurrencyName,
                          double centsPerPoint, double defaultCentsPerPoint) {
        this.rewardCurrencyName = rewardCurrencyName;
        this.centsPerPoint = centsPerPoint;
        this.defaultCentsPerPoint = defaultCentsPerPoint;
    }
}
