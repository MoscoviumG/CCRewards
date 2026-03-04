package com.example.ccrewards.worker;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.ccrewards.ui.settings.SettingsFragment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class WorkManagerScheduler {

    private static final String WORK_NAME = "benefit_reminder_daily";

    /**
     * Schedules (or re-schedules) the daily reminder, aligning the first run to the
     * user's chosen hour and minute.  Uses REPLACE so that a preference change takes
     * effect immediately without waiting for the old period to expire.
     */
    public static void scheduleBenefitReminders(Context context, SharedPreferences prefs) {
        int hour = prefs.getInt(SettingsFragment.PREF_NOTIF_TIME_HOUR, 9);
        int minute = prefs.getInt(SettingsFragment.PREF_NOTIF_TIME_MINUTE, 0);

        long initialDelayMinutes = computeInitialDelayMinutes(hour, minute);

        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                BenefitReminderWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                request);
    }

    /** Overload for callers that don't have a SharedPreferences reference handy. */
    public static void scheduleBenefitReminders(Context context) {
        android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        scheduleBenefitReminders(context,
                android.preference.PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static void cancelBenefitReminders(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME);
    }

    /**
     * Returns how many minutes from now until the next occurrence of hour:minute.
     * Minimum 1 minute to avoid scheduling in the past.
     */
    private static long computeInitialDelayMinutes(int targetHour, int targetMinute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = now.withHour(targetHour).withMinute(targetMinute)
                .withSecond(0).withNano(0);
        if (!target.isAfter(now)) {
            target = target.plusDays(1);
        }
        long minutes = now.until(target, ChronoUnit.MINUTES);
        return Math.max(minutes, 1);
    }
}
