package com.example.ccrewards.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "custom_categories")
public class CustomCategory {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String name;
    public int sortOrder;

    public CustomCategory(@NonNull String name, int sortOrder) {
        this.name = name;
        this.sortOrder = sortOrder;
    }
}
