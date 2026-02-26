package com.example.ccrewards.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateUtil {

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String toDisplayString(LocalDate date) {
        if (date == null) return "—";
        return date.format(DISPLAY_FMT);   // e.g. "Feb 25, 2026"
    }

    public static String toIsoString(LocalDate date) {
        if (date == null) return "";
        return date.format(ISO_FMT);       // e.g. "2026-02-25"
    }

    public static LocalDate fromIsoString(String iso) {
        if (iso == null || iso.isEmpty()) return null;
        return LocalDate.parse(iso, ISO_FMT);
    }

    /** Returns "Opened Feb 25, 2026" or "Active since Feb 2024" style string. */
    public static String formatCardAge(LocalDate openDate, LocalDate closeDate) {
        if (closeDate != null) {
            return "Closed " + toDisplayString(closeDate);
        }
        return "Opened " + toDisplayString(openDate);
    }
}
