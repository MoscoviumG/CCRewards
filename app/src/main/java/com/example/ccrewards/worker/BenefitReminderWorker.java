package com.example.ccrewards.worker;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.preference.PreferenceManager;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.ui.settings.SettingsFragment;
import com.example.ccrewards.util.CurrencyUtil;
import com.example.ccrewards.util.PeriodKeyUtil;

import java.util.List;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;

public class BenefitReminderWorker extends Worker {

    private static final String CHANNEL_ID = "benefit_reminders";
    private static final int NOTIFICATION_ID_BASE = 1000;

    public BenefitReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface BenefitReminderEntryPoint {
        CardRepository cardRepository();
        BenefitRepository benefitRepository();
    }

    @NonNull
    @Override
    public Result doWork() {
        Context appContext = getApplicationContext();
        createNotificationChannel(appContext);

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return Result.success();
        }

        BenefitReminderEntryPoint entryPoint = EntryPointAccessors.fromApplication(
                appContext, BenefitReminderEntryPoint.class);
        CardRepository cardRepo = entryPoint.cardRepository();
        BenefitRepository benefitRepo = entryPoint.benefitRepository();

        // Use sync queries since doWork() runs on background thread
        List<UserCard> activeCards = cardRepo.getAllActiveUserCardsSync();
        List<CardBenefit> allBenefits = benefitRepo.getAllBenefitsSync();

        if (activeCards == null || allBenefits == null) return Result.success();

        int threshold = PreferenceManager.getDefaultSharedPreferences(appContext)
                .getInt(SettingsFragment.PREF_NOTIF_DAYS_THRESHOLD, 7);

        int notifId = NOTIFICATION_ID_BASE;
        for (UserCard card : activeCards) {
            for (CardBenefit benefit : allBenefits) {
                if (!benefit.cardDefinitionId.equals(card.cardDefinitionId)) continue;

                int daysLeft = PeriodKeyUtil.daysUntilReset(benefit.resetPeriod);
                if (daysLeft > threshold) continue;

                String periodKey = PeriodKeyUtil.getCurrentPeriodKey(benefit.resetPeriod);
                BenefitUsage usage = benefitRepo.getUsageSync(card.id, benefit.id, periodKey);

                if (usage == null || !usage.isUsed) {
                    String amount = CurrencyUtil.centsToString(benefit.amountCents);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_nav_credits)
                            .setContentTitle("Unused benefit: " + benefit.name)
                            .setContentText(amount + " expires in " + daysLeft + " days")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setAutoCancel(true);

                    NotificationManagerCompat.from(appContext).notify(notifId++, builder.build());
                }
            }
        }

        return Result.success();
    }

    private void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Benefit Reminders",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Reminders for unused card benefits near their reset date");
        NotificationManager nm = context.getSystemService(NotificationManager.class);
        if (nm != null) nm.createNotificationChannel(channel);
    }
}
