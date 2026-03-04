package com.example.ccrewards.ui.credits;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.ResetType;
import com.example.ccrewards.data.model.relations.BenefitWithUsage;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.util.PeriodKeyUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CreditsViewModel extends ViewModel {

    /** Flat list item: either a section header or a benefit row. */
    public static class ListItem {
        public static final int TYPE_HEADER = 0;
        public static final int TYPE_BENEFIT = 1;

        public final int type;
        public final String headerLabel;        // for TYPE_HEADER
        public final int daysUntilReset;        // for TYPE_HEADER (calendar-based)
        public final BenefitWithUsage benefitWithUsage; // for TYPE_BENEFIT
        public final boolean isAnniversary;     // for TYPE_BENEFIT
        public final int benefitDaysUntilReset; // for TYPE_BENEFIT, meaningful when isAnniversary=true

        public ListItem(String headerLabel, int daysUntilReset) {
            this.type = TYPE_HEADER;
            this.headerLabel = headerLabel;
            this.daysUntilReset = daysUntilReset;
            this.benefitWithUsage = null;
            this.isAnniversary = false;
            this.benefitDaysUntilReset = 0;
        }

        public ListItem(BenefitWithUsage benefitWithUsage, boolean isAnniversary,
                        int benefitDaysUntilReset) {
            this.type = TYPE_BENEFIT;
            this.headerLabel = null;
            this.daysUntilReset = 0;
            this.benefitWithUsage = benefitWithUsage;
            this.isAnniversary = isAnniversary;
            this.benefitDaysUntilReset = benefitDaysUntilReset;
        }
    }

    /** Internal container used during recompute to carry anniversary metadata. */
    private static class BenefitEntry {
        final BenefitWithUsage bwu;
        final boolean isAnniversary;
        final int annivDays;

        BenefitEntry(BenefitWithUsage bwu, boolean isAnniversary, int annivDays) {
            this.bwu = bwu;
            this.isAnniversary = isAnniversary;
            this.annivDays = annivDays;
        }
    }

    // Reset period display order
    private static final ResetPeriod[] PERIOD_ORDER = {
            ResetPeriod.MONTHLY, ResetPeriod.QUARTERLY, ResetPeriod.SEMI_ANNUALLY, ResetPeriod.ANNUALLY
    };

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

    public LiveData<List<ListItem>> getDisplayItems() {
        return displayItems;
    }

    public void refresh() {
        refreshTrigger.setValue(System.currentTimeMillis());
    }

    public void setHideUsed(boolean hide) {
        hideUsed.setValue(hide);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query != null ? query : "");
    }

    public void markUsed(long userCardId, long benefitId, ResetPeriod period,
                         ResetType resetType, LocalDate openDate, boolean isUsed) {
        String periodKey = (resetType == ResetType.ANNIVERSARY && openDate != null)
                ? PeriodKeyUtil.getCurrentAnniversaryPeriodKey(period, openDate)
                : PeriodKeyUtil.getCurrentPeriodKey(period);
        benefitRepository.setUsed(userCardId, benefitId, periodKey, isUsed);
        refresh();
    }

    private void recompute(List<UserCardWithDetails> cards) {
        if (cards == null) return;

        executor.execute(() -> {
            boolean doHideUsed = Boolean.TRUE.equals(hideUsed.getValue());
            String query = searchQuery.getValue() != null
                    ? searchQuery.getValue().trim().toLowerCase(java.util.Locale.US) : "";

            // Group entries by reset period
            Map<ResetPeriod, List<BenefitEntry>> grouped = new LinkedHashMap<>();
            for (ResetPeriod p : PERIOD_ORDER) {
                grouped.put(p, new ArrayList<>());
            }

            for (UserCardWithDetails item : cards) {
                if (item.definition == null || item.benefits == null) continue;
                for (CardBenefit benefit : item.benefits) {
                    boolean isAnniv = benefit.resetType == ResetType.ANNIVERSARY
                            && item.userCard.openDate != null;
                    String periodKey = isAnniv
                            ? PeriodKeyUtil.getCurrentAnniversaryPeriodKey(
                                    benefit.resetPeriod, item.userCard.openDate)
                            : PeriodKeyUtil.getCurrentPeriodKey(benefit.resetPeriod);
                    int annivDays = isAnniv
                            ? PeriodKeyUtil.daysUntilAnniversaryReset(
                                    benefit.resetPeriod, item.userCard.openDate)
                            : 0;

                    BenefitUsage usage = benefitRepository.getUsageSync(
                            item.userCard.id, benefit.id, periodKey);
                    BenefitWithUsage bwu = new BenefitWithUsage(
                            item.userCard, item.definition, benefit, usage);

                    // Hide used filter
                    if (doHideUsed && bwu.isUsed()) continue;

                    // Search filter
                    if (!query.isEmpty()) {
                        String cardName = item.definition.displayName.toLowerCase(java.util.Locale.US);
                        String benefitName = benefit.name.toLowerCase(java.util.Locale.US);
                        if (!cardName.contains(query) && !benefitName.contains(query)) continue;
                    }

                    List<BenefitEntry> bucket = grouped.get(benefit.resetPeriod);
                    if (bucket != null) {
                        bucket.add(new BenefitEntry(bwu, isAnniv, annivDays));
                    }
                }
            }

            // Build flat list with section headers (skip empty sections)
            List<ListItem> result = new ArrayList<>();
            for (ResetPeriod period : PERIOD_ORDER) {
                List<BenefitEntry> entries = grouped.get(period);
                if (entries == null || entries.isEmpty()) continue;

                int days = PeriodKeyUtil.daysUntilReset(period);
                result.add(new ListItem(formatPeriodLabel(period), days));
                for (BenefitEntry entry : entries) {
                    result.add(new ListItem(entry.bwu, entry.isAnniversary, entry.annivDays));
                }
            }

            displayItems.postValue(result);
        });
    }

    private String formatPeriodLabel(ResetPeriod period) {
        switch (period) {
            case MONTHLY: return "Monthly Benefits";
            case QUARTERLY: return "Quarterly Benefits";
            case SEMI_ANNUALLY: return "Semi-Annual Benefits";
            default: return "Annual Benefits";
        }
    }
}
