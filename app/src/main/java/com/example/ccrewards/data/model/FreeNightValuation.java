package com.example.ccrewards.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * User-editable valuation for a specific type of free night certificate.
 * The {@link #typeKey} is the natural primary key (e.g. "HILTON_UNLIMITED",
 * "MARRIOTT_35000", "HYATT_CAT_4") and must match the typeKey stored in
 * {@link FreeNightAward}.
 *
 * <p>Seeding uses INSERT OR IGNORE so user edits to {@link #valueCents} are preserved
 * across seed refreshes. {@link #defaultValueCents} is updated separately on each refresh.
 */
@Entity(tableName = "free_night_valuations")
public class FreeNightValuation {

    @PrimaryKey
    @NonNull
    public String typeKey;

    /** Human-readable name, e.g. "Marriott Free Night (35k)". */
    @NonNull
    public String label;

    public HotelGroup hotelGroup;

    public FreeNightLimitType limitType;

    /** For POINTS_CAP type — maximum point redemption value (e.g. 35000). Null otherwise. */
    @Nullable
    public Integer pointsCap;

    /** For HYATT_CATEGORY type — Hyatt category number 1–7. Null otherwise. */
    @Nullable
    public Integer hyattCategory;

    /** User-edited value in cents (e.g. 17500 = $175). */
    @ColumnInfo(defaultValue = "0")
    public int valueCents;

    /** Default/baseline value used for the "Reset to Default" action. */
    @ColumnInfo(defaultValue = "0")
    public int defaultValueCents;

    /** Room constructor. */
    public FreeNightValuation(@NonNull String typeKey, @NonNull String label,
                              HotelGroup hotelGroup, FreeNightLimitType limitType,
                              @Nullable Integer pointsCap, @Nullable Integer hyattCategory,
                              int defaultValueCents) {
        this.typeKey = typeKey;
        this.label = label;
        this.hotelGroup = hotelGroup;
        this.limitType = limitType;
        this.pointsCap = pointsCap;
        this.hyattCategory = hyattCategory;
        this.valueCents = defaultValueCents;
        this.defaultValueCents = defaultValueCents;
    }
}
