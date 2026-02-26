package com.example.ccrewards.data.seed;

import com.example.ccrewards.data.model.PointValuation;

import java.util.Arrays;
import java.util.List;

/**
 * Pre-seeded point valuations (cents per point).
 * Sourced from TPG Feb 2026 + uscreditcardguide.com consensus values.
 */
public class DefaultPointValuations {

    public static List<PointValuation> getValuations() {
        return Arrays.asList(
            new PointValuation("Chase Ultimate Rewards Points",   2.0, 2.0),
            new PointValuation("Amex Membership Rewards Points",  2.2, 2.2),
            new PointValuation("Capital One Miles",               1.7, 1.7),
            new PointValuation("Citi ThankYou Points",            1.8, 1.8),
            new PointValuation("Bilt Points",                     2.2, 2.2),
            new PointValuation("Bilt Cash",                       1.0, 1.0),
            new PointValuation("Atmos/Alaska Rewards Miles",      1.8, 1.8),
            new PointValuation("Delta SkyMiles",                  1.2, 1.2),
            new PointValuation("Southwest Rapid Rewards",         1.5, 1.5),
            new PointValuation("United MileagePlus",              1.5, 1.5),
            new PointValuation("AAdvantage Miles",                1.5, 1.5),
            new PointValuation("Hilton Honors Points",            0.6, 0.6),
            new PointValuation("Marriott Bonvoy Points",          0.9, 0.9),
            new PointValuation("World of Hyatt Points",           1.7, 1.7),
            new PointValuation("Cash Back",                       1.0, 1.0)
        );
    }
}
