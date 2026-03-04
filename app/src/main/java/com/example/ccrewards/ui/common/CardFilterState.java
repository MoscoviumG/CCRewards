package com.example.ccrewards.ui.common;

import java.util.HashSet;
import java.util.Set;

/** Holds all active filter dimensions for card lists (My Cards and Add Card). */
public class CardFilterState {

    public enum CardType { ALL, PERSONAL, BUSINESS }
    public enum AnniversaryFilter { ANY, THIS_MONTH, NEXT_MONTH }
    public enum CardAgeFilter { ANY, LESS_THAN_1, ONE_TO_THREE, MORE_THAN_THREE }

    public CardType cardType = CardType.ALL;
    /** Empty = all issuers allowed. */
    public Set<String> issuers = new HashSet<>();
    /** Empty = all networks allowed. */
    public Set<String> networks = new HashSet<>();

    // My Cards only
    public AnniversaryFilter anniversaryMonth = AnniversaryFilter.ANY;
    public CardAgeFilter cardAge = CardAgeFilter.ANY;

    public CardFilterState() {}

    /** Deep-copy constructor. */
    public CardFilterState(CardFilterState src) {
        this.cardType = src.cardType;
        this.issuers = new HashSet<>(src.issuers);
        this.networks = new HashSet<>(src.networks);
        this.anniversaryMonth = src.anniversaryMonth;
        this.cardAge = src.cardAge;
    }

    public boolean isDefault() {
        return cardType == CardType.ALL && issuers.isEmpty() && networks.isEmpty()
                && anniversaryMonth == AnniversaryFilter.ANY
                && cardAge == CardAgeFilter.ANY;
    }

    /** Number of active filter dimensions (for badge display). */
    public int countActiveFilters() {
        int count = 0;
        if (cardType != CardType.ALL) count++;
        if (!issuers.isEmpty()) count++;
        if (!networks.isEmpty()) count++;
        if (anniversaryMonth != AnniversaryFilter.ANY) count++;
        if (cardAge != CardAgeFilter.ANY) count++;
        return count;
    }
}
