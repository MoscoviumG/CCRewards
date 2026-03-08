package com.example.ccrewards.ui.credits;

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

@HiltViewModel
public class CreditsViewModel extends ViewModel {

    /** A single benefit row in the flat sorted list. */
    public static class ListItem {
        public final BenefitWithUsage benefitWithUsage;
        public final int daysUntilReset;
        public final boolean isAnniversary;
        public final int usedCents;

        public ListItem(BenefitWithUsage bwu, int daysUntilReset, boolean isAnniversary) {
            this.benefitWithUsage = bwu;
            this.daysUntilReset = daysUntilReset;
            this.isAnniversary = isAnniversary;
            this.usedCents = bwu.usage != null ? bwu.usage.usedCents : 0;
        }
    }

    private final CardRepository cardRepository;
    private final BenefitRepository benefitRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<ListItem>> displayItems = new MutableLiveData<>();
    private final MutableLiveData<Long> refreshTrigger = new MutableLiveData<>(0L);
    private final MutableLiveData<Boolean> hideUsed = new MutableLiveData<>(false);
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    private final MediatorLiveData<Void> trigger = new MediatorLiveData<>();
    private LiveData<List<UserCardWithDetails>> sourceCards;

    @Inject
    public CreditsViewModel(CardRepository cardRepository, BenefitRepository benefitRepository) {
        this.cardRepository = cardRepository;
        this.benefitRepository = benefitRepository;

        sourceCards = cardRepository.getActiveUserCards();
        trigger.addSource(sourceCards, v -> recompute(sourceCards.getValue()));
        trigger.addSource(refreshTrigger, v -> recompute(sourceCards.getValue()));
        trigger.addSource(hideUsed, v -> recompute(sourceCards.getValue()));
        trigger.addSource(searchQuery, v -> recompute(sourceCards.getValue()));
        trigger.observeForever(v -> {});
    }

    public LiveData<List<ListItem>> getDisplayItems() { return displayItems; }

    public void refresh() { refreshTrigger.setValue(System.currentTimeMillis()); }

    public void setHideUsed(boolean hide) { hideUsed.setValue(hide); }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query != null ? query : "");
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
                    if (isCustom) {
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

                    result.add(new ListItem(bwu, daysUntilReset, isAnniv));
                }
            }

            // Sort by days until reset ascending (soonest to expire first)
            result.sort((a, b) -> Integer.compare(a.daysUntilReset, b.daysUntilReset));

            displayItems.postValue(result);
        });
    }
}
