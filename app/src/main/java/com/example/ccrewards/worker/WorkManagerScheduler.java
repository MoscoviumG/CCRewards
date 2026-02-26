package com.example.ccrewards.worker;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class WorkManagerScheduler {

    private static final String WORK_NAME = "benefit_reminder_daily";

    public static void scheduleBenefitReminders(Context context) {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                BenefitReminderWorker.class, 1, TimeUnit.DAYS)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request);
    }

    public static void cancelBenefitReminders(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME);
    }
}
