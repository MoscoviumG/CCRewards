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
    public String lastFour;         // last 4 digits of physical card, null if not set
    public int creditLimit;
    public LocalDate openDate;
    public LocalDate closeDate;     // null = still active
    public int sortOrder;

    /** User-overridden card strip color (ARGB). Null = use definition's cardColorPrimary. */
    public Long customColorPrimary;

    public UserCard(String cardDefinitionId, String nickname, int creditLimit,
                    LocalDate openDate, LocalDate closeDate, int sortOrder) {
        this.cardDefinitionId = cardDefinitionId;
        this.nickname = nickname;
        this.creditLimit = creditLimit;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.sortOrder = sortOrder;
    }

    /**
     * Returns "Display Name (···· 1234) "nickname"" with optional parts omitted when null/empty.
     */
    public static String label(String defDisplayName, String lastFour, String nickname) {
        StringBuilder sb = new StringBuilder(defDisplayName);
        if (lastFour != null && !lastFour.isEmpty()) {
            sb.append(" (").append(lastFour).append(")");
        }
        if (nickname != null && !nickname.isEmpty()) {
            sb.append(" \u201C").append(nickname).append("\u201D");
        }
        return sb.toString();
    }
}
