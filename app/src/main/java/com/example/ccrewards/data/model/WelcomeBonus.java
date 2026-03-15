package com.example.ccrewards.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "welcome_bonuses")
public class WelcomeBonus {

    /** 1:1 with user_cards.id — no autoGenerate; the user card IS the PK. */
    @PrimaryKey
    public long userCardId;

    /** Raw bonus amount. For cash-back cards: cents (e.g. 20000 = $200). For points: raw count (e.g. 60000). */
    public int bonusPoints;

    /** From the card's rewardCurrencyName, e.g. "Chase Ultimate Rewards Points" or "Cash Back". */
    @NonNull
    public String bonusCurrencyName = "";

    /** Spend required to earn the bonus, in cents. */
    public int spendRequirementCents;

    /** How much the user has already spent toward the requirement, in cents. */
    @ColumnInfo(defaultValue = "0")
    public int spendUsedCents;

    /** Optional deadline. Null = no deadline. Stored via Converters as epoch day. */
    public LocalDate deadline;

    /** When true, appears in the Best Card banner. Default true. */
    @ColumnInfo(defaultValue = "1")
    public boolean showInBestCard;

    /** Set to true when the user marks it achieved. Stays in DB but leaves the banner. */
    @ColumnInfo(defaultValue = "0")
    public boolean achieved;

    /**
     * Additional cash component of the welcome bonus, in cents.
     * Used for mixed bonuses like "100k points + $200 statement credit".
     * Zero means no additional cash component.
     */
    @ColumnInfo(defaultValue = "0")
    public int cashbackCents;

    /**
     * typeKey of a free night certificate earned with this WB (e.g. "MARRIOTT_35000").
     * Null if no free night is part of the WB. The FreeNightAward is created only when
     * the user marks this WB as achieved, then this field is no longer needed.
     */
    @Nullable
    public String fnTypeKey;

    /** How many free night certificates are included. Relevant only when fnTypeKey != null. */
    @ColumnInfo(defaultValue = "1")
    public int fnCount = 1;
}
