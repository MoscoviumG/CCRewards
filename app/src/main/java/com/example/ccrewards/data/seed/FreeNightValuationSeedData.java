package com.example.ccrewards.data.seed;

import com.example.ccrewards.data.model.FreeNightLimitType;
import com.example.ccrewards.data.model.FreeNightValuation;
import com.example.ccrewards.data.model.HotelGroup;

import java.util.Arrays;
import java.util.List;

public class FreeNightValuationSeedData {

    public static List<FreeNightValuation> getValuations() {
        return Arrays.asList(
            // ── Hilton ──────────────────────────────────────────────────────
            freeNight("HILTON_UNLIMITED",
                    "Hilton Free Night (Unlimited)",
                    HotelGroup.HILTON, FreeNightLimitType.UNLIMITED,
                    null, null, 50000),

            // ── Marriott ─────────────────────────────────────────────────────
            freeNight("MARRIOTT_35000",
                    "Marriott Free Night (35k)",
                    HotelGroup.MARRIOTT, FreeNightLimitType.POINTS_CAP,
                    35000, null, 17500),
            freeNight("MARRIOTT_85000",
                    "Marriott Free Night (85k)",
                    HotelGroup.MARRIOTT, FreeNightLimitType.POINTS_CAP,
                    85000, null, 35000),

            // ── IHG ──────────────────────────────────────────────────────────
            freeNight("IHG_40000",
                    "IHG Free Night (40k)",
                    HotelGroup.IHG, FreeNightLimitType.POINTS_CAP,
                    40000, null, 15000),
            freeNight("IHG_60000",
                    "IHG Free Night (60k)",
                    HotelGroup.IHG, FreeNightLimitType.POINTS_CAP,
                    60000, null, 20000),
            freeNight("IHG_100000",
                    "IHG Free Night (100k)",
                    HotelGroup.IHG, FreeNightLimitType.POINTS_CAP,
                    100000, null, 30000),

            // ── World of Hyatt ───────────────────────────────────────────────
            freeNight("HYATT_CAT_1",
                    "World of Hyatt Free Night (Cat 1)",
                    HotelGroup.HYATT, FreeNightLimitType.HYATT_CATEGORY,
                    null, 1, 7500),
            freeNight("HYATT_CAT_2",
                    "World of Hyatt Free Night (Cat 2)",
                    HotelGroup.HYATT, FreeNightLimitType.HYATT_CATEGORY,
                    null, 2, 10000),
            freeNight("HYATT_CAT_3",
                    "World of Hyatt Free Night (Cat 3)",
                    HotelGroup.HYATT, FreeNightLimitType.HYATT_CATEGORY,
                    null, 3, 12500),
            freeNight("HYATT_CAT_4",
                    "World of Hyatt Free Night (Cat 4)",
                    HotelGroup.HYATT, FreeNightLimitType.HYATT_CATEGORY,
                    null, 4, 17500),
            freeNight("HYATT_CAT_5",
                    "World of Hyatt Free Night (Cat 5)",
                    HotelGroup.HYATT, FreeNightLimitType.HYATT_CATEGORY,
                    null, 5, 22500),
            freeNight("HYATT_CAT_6",
                    "World of Hyatt Free Night (Cat 6)",
                    HotelGroup.HYATT, FreeNightLimitType.HYATT_CATEGORY,
                    null, 6, 30000),
            freeNight("HYATT_CAT_7",
                    "World of Hyatt Free Night (Cat 7)",
                    HotelGroup.HYATT, FreeNightLimitType.HYATT_CATEGORY,
                    null, 7, 40000),

            // ── Wyndham ──────────────────────────────────────────────────────
            freeNight("WYNDHAM_30000",
                    "Wyndham Free Night (30k)",
                    HotelGroup.WYNDHAM, FreeNightLimitType.POINTS_CAP,
                    30000, null, 10000)
        );
    }

    private static FreeNightValuation freeNight(String typeKey, String label,
            HotelGroup hotelGroup, FreeNightLimitType limitType,
            Integer pointsCap, Integer hyattCategory, int defaultValueCents) {
        return new FreeNightValuation(typeKey, label, hotelGroup, limitType,
                pointsCap, hyattCategory, defaultValueCents);
    }
}
