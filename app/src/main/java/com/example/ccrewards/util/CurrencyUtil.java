package com.example.ccrewards.util;

import java.util.Locale;

public class CurrencyUtil {

    /** Converts cents (int) to a "$12.50" style string. */
    public static String centsToString(int cents) {
        return String.format(Locale.US, "$%,.2f", cents / 100.0);
    }

    /** Formats an effective return percentage: "3.52%" */
    public static String formatEffectiveReturn(double effectiveReturn) {
        return String.format(Locale.US, "%.2f%%", effectiveReturn);
    }

    /** Formats a rate: "3x" or "3%" depending on rateType label. */
    public static String formatRate(double rate, String rateTypeLabel) {
        if (rateTypeLabel.equals("CASHBACK") || rateTypeLabel.equals("BILT_CASH")) {
            return String.format(Locale.US, "%.1f%%", rate);
        } else {
            // Strip trailing .0 for whole numbers
            if (rate == Math.floor(rate)) {
                return (int) rate + "x";
            }
            return String.format(Locale.US, "%.1fx", rate);
        }
    }

    /** Formats cents-per-point: "2.20¢" */
    public static String formatCentsPerPoint(double cpp) {
        return String.format(Locale.US, "%.2f¢", cpp);
    }

    /** Formats annual fee: "$95/yr" or "No fee" */
    public static String formatAnnualFee(int annualFeeDollars) {
        if (annualFeeDollars == 0) return "No annual fee";
        return "$" + annualFeeDollars + "/yr";
    }
}
