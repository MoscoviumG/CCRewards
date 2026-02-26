package com.example.ccrewards.util;

import com.example.ccrewards.data.model.ResetPeriod;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Generates the period key string used in BenefitUsage.periodKey. */
public class PeriodKeyUtil {

    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    public static String getCurrentPeriodKey(ResetPeriod period) {
        return getPeriodKey(LocalDate.now(), period);
    }

    public static String getPeriodKey(LocalDate date, ResetPeriod period) {
        int year = date.getYear();
        int month = date.getMonthValue();

        switch (period) {
            case MONTHLY:
                return date.format(MONTH_FMT);        // "2026-02"

            case QUARTERLY:
                int quarter = (month - 1) / 3 + 1;
                return year + "-Q" + quarter;          // "2026-Q1"

            case SEMI_ANNUALLY:
                int half = month <= 6 ? 1 : 2;
                return year + "-H" + half;             // "2026-H1"

            case ANNUALLY:
            default:
                return String.valueOf(year);           // "2026"
        }
    }

    /** How many days until the current period resets (approximate). */
    public static int daysUntilReset(ResetPeriod period) {
        LocalDate today = LocalDate.now();
        LocalDate nextReset;

        switch (period) {
            case MONTHLY:
                nextReset = today.withDayOfMonth(1).plusMonths(1);
                break;
            case QUARTERLY:
                int currentQuarter = (today.getMonthValue() - 1) / 3;
                nextReset = LocalDate.of(today.getYear(), currentQuarter * 3 + 1, 1).plusMonths(3);
                break;
            case SEMI_ANNUALLY:
                nextReset = today.getMonthValue() <= 6
                        ? LocalDate.of(today.getYear(), 7, 1)
                        : LocalDate.of(today.getYear() + 1, 1, 1);
                break;
            case ANNUALLY:
            default:
                nextReset = LocalDate.of(today.getYear() + 1, 1, 1);
                break;
        }

        return (int) today.until(nextReset, java.time.temporal.ChronoUnit.DAYS);
    }
}
