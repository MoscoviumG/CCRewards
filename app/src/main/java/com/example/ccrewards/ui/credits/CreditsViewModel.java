package com.example.ccrewards.ui.credits;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.ResetType;
import com.example.ccrewards.data.model.relations.BenefitWithUsage;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.util.PeriodKeyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class CreditsViewModel extends ViewModel {

    /** A single benefit row in the flat sorted list. */
    public static class ListItem {
        public final BenefitWithUsage benefitWithUsage;
        public final int daysUntilReset;
        public final boolean isAnniversary;
        public final int usedCents;
        public final boolean isStarred;

        public ListItem(BenefitWithUsage bwu, int daysUntilReset, boolean isAnniversary,
                        boolean isStarred) {
            this.benefitWithUsage = bwu;
            this.daysUntilReset = daysUntilReset;
            this.isAnniversary = isAnniversary;
            this.usedCents = bwu.usage != null ? bwu.usage.usedCents : 0;
            this.isStarred = isStarred;
        }
    }

    private static final String PREFS_NAME = "credits_prefs";
    private static final String KEY_HIDE_USED = "hide_used";

    private final CardRepository cardRepository;
    private final BenefitRepository benefitRepository;
    private final SharedPreferences prefs;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<ListItem>> displayItems = new MutableLiveData<>();
    private final MutableLiveData<Long> refreshTrigger = new MutableLiveData<>(0L);
    private final MutableLiveData<Boolean> hideUsed;
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    private final MediatorLiveData<Void> trigger = new MediatorLiveData<>();
    private LiveData<List<UserCardWithDetails>> sourceCards;

    @Inject
    public CreditsViewModel(@ApplicationContext Context context,
                            CardRepository cardRepository, BenefitRepository benefitRepository) {
        this.cardRepository = cardRepository;
        this.benefitRepository = benefitRepository;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.hideUsed = new MutableLiveData<>(prefs.getBoolean(KEY_HIDE_USED, false));

        sourceCards = cardRepository.getOpenUserCards();
        trigger.addSource(sourceCards, v -> recompute(sourceCards.getValue()));
        trigger.addSource(refreshTrigger, v -> recompute(sourceCards.getValue()));
        trigger.addSource(hideUsed, v -> recompute(sourceCards.getValue()));
        trigger.addSource(searchQuery, v -> recompute(sourceCards.getValue()));
        trigger.observeForever(v -> {});
    }

    public LiveData<List<ListItem>> getDisplayItems() { return displayItems; }

    public void refresh() { refreshTrigger.setValue(System.currentTimeMillis()); }

    public boolean getInitialHideUsed() { return Boolean.TRUE.equals(hideUsed.getValue()); }

    public void setHideUsed(boolean hide) {
        hideUsed.setValue(hide);
        prefs.edit().putBoolean(KEY_HIDE_USED, hide).apply();
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query != null ? query : "");
    }

    public void toggleStar(long userCardId, long benefitId) {
        benefitRepository.toggleStar(userCardId, benefitId,
                () -> refreshTrigger.postValue(System.currentTimeMillis()));
    }

    private void recompute(List<UserCardWithDetails> cards) {
        if (cards == null) return;

        executor.execute(() -> {
            boolean doHideUsed = Boolean.TRUE.equals(hideUsed.getValue());
            String query = searchQuery.getValue() != null
                    ? searchQuery.getValue().trim().toLowerCase(java.util.Locale.US) : "";

            List<ListItem> result = new ArrayList<>();

            for (UserCardWithDetails item : cards) {
                if (item.definition == null || item.benefits == null) continue;
                for (CardBenefit benefit : item.benefits) {
                    boolean isCustom = benefit.resetType == ResetType.CUSTOM
                            && benefit.customResetMonth != null && benefit.customResetDay != null;
                    boolean isAnniv = !isCustom && benefit.resetType == ResetType.ANNIVERSARY
                            && item.userCard.openDate != null;
                    String periodKey;
                    int daysUntilReset;
                    if (benefit.isOneTime) {
                        periodKey = "one-time";
                        // Use due date (customResetMonth/Day) for sorting if set
                        boolean hasDueDate = benefit.customResetMonth != null && benefit.customResetDay != null;
                        daysUntilReset = hasDueDate
                                ? PeriodKeyUtil.daysUntilCustomReset(
                                        com.example.ccrewards.data.model.ResetPeriod.ANNUALLY, benefit.customResetMonth, benefit.customResetDay)
                                : Integer.MAX_VALUE;
                    } else if (isCustom) {
                        periodKey = PeriodKeyUtil.getCurrentCustomPeriodKey(
                                benefit.resetPeriod, benefit.customResetMonth, benefit.customResetDay);
                        daysUntilReset = PeriodKeyUtil.daysUntilCustomReset(
                                benefit.resetPeriod, benefit.customResetMonth, benefit.customResetDay);
                    } else if (isAnniv) {
                        periodKey = PeriodKeyUtil.getCurrentAnniversaryPeriodKey(
                                benefit.resetPeriod, item.userCard.openDate);
                        daysUntilReset = PeriodKeyUtil.daysUntilAnniversaryReset(
                                benefit.resetPeriod, item.userCard.openDate);
                    } else {
                        periodKey = PeriodKeyUtil.getCurrentPeriodKey(benefit.resetPeriod);
                        daysUntilReset = PeriodKeyUtil.daysUntilReset(benefit.resetPeriod);
                    }

                    BenefitUsage usage = benefitRepository.getUsageSync(
                            item.userCard.id, benefit.id, periodKey);
                    BenefitWithUsage bwu = new BenefitWithUsage(
                            item.userCard, item.definition, benefit, usage);

                    if (doHideUsed && bwu.isUsed()) continue;

                    if (!query.isEmpty()) {
                        String cardName = item.definition.displayName.toLowerCase(java.util.Locale.US);
                        String benefitName = benefit.name.toLowerCase(java.util.Locale.US);
                        if (!cardName.contains(query) && !benefitName.contains(query)) continue;
                    }

                    boolean starred = benefitRepository.isStarred(item.userCard.id, benefit.id);
                    result.add(new ListItem(bwu, daysUntilReset, isAnniv, starred));
                }
            }

            // 1) Starred first, 2) unused before used, 3) days until reset ascending
            result.sort((a, b) -> {
                if (a.isStarred != b.isStarred) return a.isStarred ? -1 : 1;
                boolean aUsed = a.benefitWithUsage.isUsed();
                boolean bUsed = b.benefitWithUsage.isUsed();
                if (aUsed != bUsed) return aUsed ? 1 : -1;
                return Integer.compare(a.daysUntilReset, b.daysUntilReset);
            });

            displayItems.postValue(result);
        });
    }
}
