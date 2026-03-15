package com.example.ccrewards.data.model;

public enum FreeNightLimitType {
    /** No points cap — redeemable at any property (e.g. Hilton free night on Aspire). */
    UNLIMITED,
    /** Capped at a maximum points value (e.g. Marriott 35k, IHG 40k). */
    POINTS_CAP,
    /** Redeemable up to a specific Hyatt category (1–7). */
    HYATT_CATEGORY
}
