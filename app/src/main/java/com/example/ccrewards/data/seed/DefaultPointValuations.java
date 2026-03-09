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
            // Transferable bank points (1¢ baseline)
            new PointValuation("Chase Ultimate Rewards Points",   1.0,  1.0),
            new PointValuation("Amex Membership Rewards Points",  1.0,  1.0),
            new PointValuation("Capital One Miles",               1.0,  1.0),
            new PointValuation("Citi ThankYou Points",            1.0,  1.0),
            new PointValuation("Bilt Points",                     1.0,  1.0),
            new PointValuation("Bilt Cash",                       1.0,  1.0),
            // Airline miles (1¢ baseline)
            new PointValuation("Atmos/Alaska Rewards Miles",      1.0,  1.0),
            new PointValuation("Delta SkyMiles",                  1.0,  1.0),
            new PointValuation("Southwest Rapid Rewards",         1.0,  1.0),
            new PointValuation("United MileagePlus",              1.0,  1.0),
            new PointValuation("AAdvantage Miles",                1.0,  1.0),
            new PointValuation("Avios",                           1.0,  1.0),
            new PointValuation("Aeroplan Miles",                  1.0,  1.0),
            // Hotel points
            new PointValuation("Hilton Honors Points",            0.4,  0.4),
            new PointValuation("Marriott Bonvoy Points",          0.4,  0.4),
            new PointValuation("World of Hyatt Points",           1.0,  1.0),
            new PointValuation("IHG One Rewards Points",          0.4,  0.4),
            // BofA fixed-value points
            new PointValuation("BofA Points",                     1.0,  1.0),
            // Airline miles — co-brand programs (1¢ baseline)
            new PointValuation("Flying Blue Miles",               1.0,  1.0),
            new PointValuation("Free Spirit Points",              1.0,  1.0),
            new PointValuation("JetBlue TrueBlue Points",         1.0,  1.0),
            new PointValuation("Wyndham Rewards Points",          1.0,  1.0),
            new PointValuation("Frontier Miles",                  1.0,  1.0),
            new PointValuation("Miles & More Miles",              1.0,  1.0),
            new PointValuation("Emirates Skywards Miles",         1.0,  1.0),
            // HSBC
            new PointValuation("HSBC Rewards Points",             1.0,  1.0),
            // Cash back (fixed)
            new PointValuation("Cash Back",                       1.0,  1.0)
        );
    }
}
