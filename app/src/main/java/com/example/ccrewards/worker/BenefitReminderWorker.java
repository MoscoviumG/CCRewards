package com.example.ccrewards.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.preference.PreferenceManager;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.ResetType;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.FreeNightRepository;
import com.example.ccrewards.data.repository.WelcomeBonusRepository;
import com.example.ccrewards.ui.settings.SettingsFragment;
import com.example.ccrewards.util.CurrencyUtil;
import com.example.ccrewards.util.PeriodKeyUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;

public class BenefitReminderWorker extends Worker {

    private static final String CHANNEL_ID = "benefit_reminders_v2"; // v2 = IMPORTANCE_HIGH
    private static final int NOTIFICATION_ID_BASE = 1000;

    public BenefitReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @EntryPoint
    @InstallIn(SingletonComponent.class)
    interface BenefitReminderEntryPoint {
        CardRepository cardRepository();
        BenefitRepository benefitRepository();
        WelcomeBonusRepository welcomeBonusRepository();
        FreeNightRepository freeNightRepository();
    }

    /** Fires an immediate "reminders are on" notification. Call when the user enables the toggle. */
    public static void sendTestNotification(Context context) {
        createNotificationChannel(context);
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        if (!nm.areNotificationsEnabled()) return;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_nav_credits)
                .setContentTitle("Benefit reminders are on")
                .setContentText("You'll be notified when benefits are about to expire unused")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        nm.notify(NOTIFICATION_ID_BASE - 1, builder.build());
    }

    @NonNull
    @Override
    public Result doWork() {
        Context appContext = getApplicationContext();
        createNotificationChannel(appContext);

        // Guard: respect the toggle even if work wasn't cancelled yet
        boolean enabled = PreferenceManager.getDefaultSharedPreferences(appContext)
                .getBoolean(SettingsFragment.PREF_NOTIFICATIONS_ENABLED, true);
        if (!enabled) return Result.success();

        if (!NotificationManagerCompat.from(appContext).areNotificationsEnabled()) {
            return Result.success();
        }

        BenefitReminderEntryPoint entryPoint = EntryPointAccessors.fromApplication(
                appContext, BenefitReminderEntryPoint.class);
        CardRepository cardRepo = entryPoint.cardRepository();
        BenefitRepository benefitRepo = entryPoint.benefitRepository();
        WelcomeBonusRepository wbRepo = entryPoint.welcomeBonusRepository();
        FreeNightRepository fnRepo = entryPoint.freeNightRepository();

        // Use sync queries since doWork() runs on background thread
        List<UserCard> activeCards = cardRepo.getOpenUserCardsSync();
        List<CardBenefit> allBenefits = benefitRepo.getAllBenefitsSync();

        if (activeCards == null || allBenefits == null) return Result.success();

        int threshold = PreferenceManager.getDefaultSharedPreferences(appContext)
                .getInt(SettingsFragment.PREF_NOTIF_DAYS_THRESHOLD, 7);

        int notifId = NOTIFICATION_ID_BASE;
        for (UserCard card : activeCards) {
            for (CardBenefit benefit : allBenefits) {
                if (!benefit.cardDefinitionId.equals(card.cardDefinitionId)) continue;

                boolean isAnniv = benefit.resetType == ResetType.ANNIVERSARY
                        && card.openDate != null;
                int daysLeft = isAnniv
                        ? PeriodKeyUtil.daysUntilAnniversaryReset(benefit.resetPeriod, card.openDate)
                        : PeriodKeyUtil.daysUntilReset(benefit.resetPeriod);
                if (daysLeft > threshold) continue;

                String periodKey = isAnniv
                        ? PeriodKeyUtil.getCurrentAnniversaryPeriodKey(benefit.resetPeriod, card.openDate)
                        : PeriodKeyUtil.getCurrentPeriodKey(benefit.resetPeriod);
                BenefitUsage usage = benefitRepo.getUsageSync(card.id, benefit.id, periodKey);

                if (usage == null || !usage.isUsed) {
                    String amount = CurrencyUtil.centsToString(benefit.amountCents);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_nav_credits)
                            .setContentTitle("Unused benefit: " + benefit.name)
                            .setContentText(amount + " expires in " + daysLeft + " days")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);

                    NotificationManagerCompat.from(appContext).notify(notifId++, builder.build());
                }
            }
        }

        // Welcome bonus notifications
        List<WelcomeBonus> activeWbs = wbRepo.getActiveSync();
        if (activeWbs != null) {
            LocalDate today = LocalDate.now();
            for (WelcomeBonus wb : activeWbs) {
                if (wb.deadline == null) continue; // no urgency without a deadline

                int daysLeft = (int) today.until(wb.deadline, ChronoUnit.DAYS);
                if (daysLeft < 0 || daysLeft > threshold) continue;

                // Find the matching active UserCard
                UserCard card = null;
                for (UserCard c : activeCards) {
                    if (c.id == wb.userCardId) { card = c; break; }
                }
                if (card == null) continue;

                CardDefinition def = cardRepo.getCardDefinitionSync(card.cardDefinitionId);
                String cardName = UserCard.label(
                        def != null ? def.displayName : "Card",
                        card.lastFour, card.nickname);

                int remainCents = Math.max(0, wb.spendRequirementCents - wb.spendUsedCents);
                String body = daysLeft == 0 ? "Deadline is today!"
                        : daysLeft + " day" + (daysLeft == 1 ? "" : "s") + " left";
                if (remainCents > 0) {
                    body += " · " + CurrencyUtil.centsToString(remainCents) + " to go";
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_nav_credits)
                        .setContentTitle("Welcome bonus expiring: " + cardName)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);
                NotificationManagerCompat.from(appContext).notify(notifId++, builder.build());
            }
        }

        // Free night expiry notifications
        List<Long> fnCardIds = new ArrayList<>();
        for (UserCard c : activeCards) fnCardIds.add(c.id);
        List<com.example.ccrewards.data.model.FreeNightAward> fnAwards =
                fnRepo.getAllAwardsForCardsSync(fnCardIds);
        if (fnAwards != null) {
            LocalDate today = LocalDate.now();
            for (com.example.ccrewards.data.model.FreeNightAward fn : fnAwards) {
                if (fn.usedCount >= fn.totalCount) continue; // already used
                if (fn.expirationDate == null) continue;
                long daysUntilExp = ChronoUnit.DAYS.between(today, fn.expirationDate);
                if (daysUntilExp < 0 || daysUntilExp > threshold) continue;
                String label = fn.label != null ? fn.label : fn.typeKey;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_nav_credits)
                        .setContentTitle("Free night expiring: " + label)
                        .setContentText("Expires in " + daysUntilExp + " day" + (daysUntilExp == 1 ? "" : "s"))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);
                NotificationManagerCompat.from(appContext).notify(notifId++, builder.build());
            }
        }

        return Result.success();
    }

    private static void createNotificationChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Benefit Reminders",
                NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Reminders for unused card benefits near their reset date");
        NotificationManager nm = context.getSystemService(NotificationManager.class);
        if (nm != null) nm.createNotificationChannel(channel);
    }
}
