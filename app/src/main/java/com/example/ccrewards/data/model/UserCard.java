package com.example.ccrewards.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "user_cards")
public class UserCard {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String cardDefinitionId;
    public String nickname;         // null if not set
    public int creditLimit;
    public LocalDate openDate;
    public LocalDate closeDate;     // null = still active
    public int sortOrder;

    public UserCard(String cardDefinitionId, String nickname, int creditLimit,
                    LocalDate openDate, LocalDate closeDate, int sortOrder) {
        this.cardDefinitionId = cardDefinitionId;
        this.nickname = nickname;
        this.creditLimit = creditLimit;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.sortOrder = sortOrder;
    }
}
