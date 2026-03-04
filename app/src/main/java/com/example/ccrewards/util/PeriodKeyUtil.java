package com.example.ccrewards.util;

import com.example.ccrewards.data.model.ResetPeriod;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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

    /**
     * Returns the start {@link LocalDate} of a period key.
     * Handles calendar keys ("2026-02", "2026-Q1", "2026-H2", "2026")
     * and anniversary keys ("anniv-2025-06-15").
     * Returns null if the key cannot be parsed.
     */
    public static LocalDate periodKeyStartDate(String key) {
        if (key == null) return null;
        try {
            if (key.startsWith("anniv-")) {
                return LocalDate.parse(key.substring(6));  // "anniv-2025-06-15" → 2025-06-15
            } else if (key.contains("-Q")) {               // "2026-Q2"
                int year = Integer.parseInt(key.substring(0, 4));
                int q = Integer.parseInt(key.substring(6));
                return LocalDate.of(year, (q - 1) * 3 + 1, 1);
            } else if (key.contains("-H")) {               // "2026-H2"
                int year = Integer.parseInt(key.substring(0, 4));
                int h = Integer.parseInt(key.substring(6));
                return LocalDate.of(year, h == 1 ? 1 : 7, 1);
            } else if (key.length() == 7) {                // "2026-02"
                return LocalDate.parse(key + "-01");
            } else {                                       // "2026"
                return LocalDate.of(Integer.parseInt(key), 1, 1);
            }
        } catch (Exception e) {
            return null;
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

        return (int) today.until(nextReset, ChronoUnit.DAYS);
    }

    // ── Anniversary-based reset methods ───────────────────────────────────────

    /**
     * Period key for an anniversary-based benefit.
     * Format: "anniv-YYYY-MM-DD" where the date is the start of the current anniversary period.
     */
    public static String getAnniversaryPeriodKey(LocalDate date, ResetPeriod period,
                                                  LocalDate openDate) {
        return "anniv-" + getAnniversaryPeriodStart(date, period, openDate).toString();
    }

    /** Returns today's anniversary period key. */
    public static String getCurrentAnniversaryPeriodKey(ResetPeriod period, LocalDate openDate) {
        return getAnniversaryPeriodKey(LocalDate.now(), period, openDate);
    }

    /** Days until the next anniversary-based reset. */
    public static int daysUntilAnniversaryReset(ResetPeriod period, LocalDate openDate) {
        LocalDate today = LocalDate.now();
        LocalDate start = getAnniversaryPeriodStart(today, period, openDate);
        LocalDate nextReset;
        switch (period) {
            case MONTHLY:       nextReset = start.plusMonths(1); break;
            case QUARTERLY:     nextReset = start.plusMonths(3); break;
            case SEMI_ANNUALLY: nextReset = start.plusMonths(6); break;
            case ANNUALLY:
            default:            nextReset = start.plusYears(1);  break;
        }
        return (int) today.until(nextReset, ChronoUnit.DAYS);
    }

    /**
     * Returns the start date of the anniversary period that contains {@code date}.
     * Uses the day-of-month (and for longer periods, month) from {@code openDate} as the anchor.
     */
    private static LocalDate getAnniversaryPeriodStart(LocalDate date, ResetPeriod period,
                                                        LocalDate openDate) {
        switch (period) {
            case MONTHLY: {
                // Reset on the same day of month as openDate (clamped to month length)
                int day = Math.min(openDate.getDayOfMonth(), date.lengthOfMonth());
                LocalDate candidate = date.withDayOfMonth(day);
                return candidate.isAfter(date) ? candidate.minusMonths(1) : candidate;
            }
            case QUARTERLY: {
                // Find the most recent quarterly anniversary on or before date
                LocalDate base = openDate.withYear(date.getYear());
                // Adjust to current year range
                while (base.isAfter(date)) base = base.minusMonths(3);
                while (!base.plusMonths(3).isAfter(date)) base = base.plusMonths(3);
                return base;
            }
            case SEMI_ANNUALLY: {
                LocalDate base = openDate.withYear(date.getYear());
                while (base.isAfter(date)) base = base.minusMonths(6);
                while (!base.plusMonths(6).isAfter(date)) base = base.plusMonths(6);
                return base;
            }
            case ANNUALLY:
            default: {
                // Reset on the same month/day as openDate each year
                LocalDate candidate = openDate.withYear(date.getYear());
                return candidate.isAfter(date) ? candidate.minusYears(1) : candidate;
            }
        }
    }
}
