package com.example.ccrewards.data.seed;

import java.util.Arrays;
import java.util.List;

public class AnnualFreeNightSeedData {

    public static class Entry {
        public final String cardDefinitionId;
        public final String typeKey;
        public final String label;

        public Entry(String cardDefId, String typeKey, String label) {
            this.cardDefinitionId = cardDefId;
            this.typeKey = typeKey;
            this.label = label;
        }
    }

    public static List<Entry> getEntries() {
        return Arrays.asList(
            new Entry("marriott_boundless",      "MARRIOTT_35000",   "Anniversary Free Night (35k)"),
            new Entry("chase_ritz_carlton",      "MARRIOTT_85000",   "Anniversary Free Night (85k)"),
            new Entry("marriott_brilliant_amex", "MARRIOTT_85000",   "Anniversary Free Night (85k)"),
            new Entry("ihg_premier",             "IHG_40000",        "Anniversary Free Night (40k)"),
            new Entry("ihg_premier_business",    "IHG_40000",        "Anniversary Free Night (40k)"),
            new Entry("hilton_aspire_amex",      "HILTON_UNLIMITED", "Annual Free Night Reward"),
            new Entry("world_of_hyatt",          "HYATT_CAT_4",      "Anniversary Free Night (Cat 1-4)"),
            new Entry("wyndham_earner_plus",     "WYNDHAM_30000",    "Anniversary Free Night (30k)")
        );
    }
}
