package com.example.ccrewards.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

/**
 * A free night certificate associated with a user card.
 * Can come from an annual card benefit (e.g. Marriott Bonvoy Boundless anniversary night)
 * or as part of a welcome bonus bundle (e.g. "100k Hilton + 1 free night").
 */
@Entity(tableName = "free_night_awards")
public class FreeNightAward {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userCardId;

    /** Optional display label, e.g. "Anniversary Night". */
    @Nullable
    public String label;

    /**
     * Key identifying the free night type; references {@link FreeNightValuation#typeKey}.
     * Examples: "HILTON_UNLIMITED", "MARRIOTT_35000", "HYATT_CAT_4".
     */
    @NonNull
    public String typeKey;

    /** Certificate expiration date (null = no expiration). */
    @Nullable
    public LocalDate expirationDate;

    /** Total number of certificates (usually 1; >1 for WB bundles). */
    @ColumnInfo(defaultValue = "1")
    public int totalCount;

    /** How many certificates have been used. */
    @ColumnInfo(defaultValue = "0")
    public int usedCount;

    /** True if this award came from a welcome bonus rather than an annual benefit. */
    @ColumnInfo(defaultValue = "0")
    public boolean isFromWelcomeBonus;

    /** True for annual card benefits that renew each year. False for one-time (WB/manual). */
    @ColumnInfo(defaultValue = "0")
    public boolean isRecurring;

    /** Month (1-12) of the annual renewal date. Null if not recurring. */
    @Nullable
    public Integer renewalMonth;

    /** Day (1-31) of the annual renewal date. Null if not recurring. */
    @Nullable
    public Integer renewalDay;

    /** Room constructor. */
    public FreeNightAward(@NonNull String typeKey) {
        this.typeKey = typeKey;
    }

    @Ignore
    public FreeNightAward(long userCardId, @NonNull String typeKey,
                          @Nullable String label, @Nullable LocalDate expirationDate,
                          int totalCount, boolean isFromWelcomeBonus) {
        this.userCardId = userCardId;
        this.typeKey = typeKey;
        this.label = label;
        this.expirationDate = expirationDate;
        this.totalCount = totalCount;
        this.usedCount = 0;
        this.isFromWelcomeBonus = isFromWelcomeBonus;
    }
}
