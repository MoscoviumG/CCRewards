package com.example.ccrewards.ui.credits;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.relations.BenefitWithUsage;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.util.PeriodKeyUtil;

import java.util.ArrayList;
import java.util.Arrays;
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
        public final String headerLabel;   // for TYPE_HEADER
        public final int daysUntilReset;   // for TYPE_HEADER
        public final BenefitWithUsage benefitWithUsage; // for TYPE_BENEFIT

        public ListItem(String headerLabel, int daysUntilReset) {
            this.type = TYPE_HEADER;
            this.headerLabel = headerLabel;
            this.daysUntilReset = daysUntilReset;
            this.benefitWithUsage = null;
        }

        public ListItem(BenefitWithUsage benefitWithUsage) {
            this.type = TYPE_BENEFIT;
            this.headerLabel = null;
            this.daysUntilReset = 0;
            this.benefitWithUsage = benefitWithUsage;
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

    private final MediatorLiveData<Void> trigger = new MediatorLiveData<>();
    private LiveData<List<UserCardWithDetails>> sourceCards;

    @Inject
    public CreditsViewModel(CardRepository cardRepository, BenefitRepository benefitRepository) {
        this.cardRepository = cardRepository;
        this.benefitRepository = benefitRepository;

        sourceCards = cardRepository.getActiveUserCards();
        trigger.addSource(sourceCards, v -> recompute(sourceCards.getValue()));
        trigger.addSource(refreshTrigger, v -> recompute(sourceCards.getValue()));
        trigger.observeForever(v -> {});
    }

    public LiveData<List<ListItem>> getDisplayItems() {
        return displayItems;
    }

    public void refresh() {
        refreshTrigger.setValue(System.currentTimeMillis());
    }

    public void markUsed(long userCardId, long benefitId, ResetPeriod period, boolean isUsed) {
        String periodKey = PeriodKeyUtil.getCurrentPeriodKey(period);
        benefitRepository.setUsed(userCardId, benefitId, periodKey, isUsed);
        refresh();
    }

    private void recompute(List<UserCardWithDetails> cards) {
        if (cards == null) return;

        executor.execute(() -> {
            // Build map of periodKey → list of BenefitWithUsage (grouped by reset period)
            Map<ResetPeriod, List<BenefitWithUsage>> grouped = new LinkedHashMap<>();
            for (ResetPeriod p : PERIOD_ORDER) {
                grouped.put(p, new ArrayList<>());
            }

            for (UserCardWithDetails item : cards) {
                if (item.definition == null || item.benefits == null) continue;
                for (CardBenefit benefit : item.benefits) {
                    String periodKey = PeriodKeyUtil.getCurrentPeriodKey(benefit.resetPeriod);
                    // Sync lookup of current-period usage
                    BenefitUsage usage = benefitRepository.getUsageSync(
                            item.userCard.id, benefit.id, periodKey);
                    BenefitWithUsage bwu = new BenefitWithUsage(
                            item.userCard, item.definition, benefit, usage);
                    List<BenefitWithUsage> bucket = grouped.get(benefit.resetPeriod);
                    if (bucket != null) bucket.add(bwu);
                }
            }

            // Build flat list with section headers
            List<ListItem> result = new ArrayList<>();
            for (ResetPeriod period : PERIOD_ORDER) {
                List<BenefitWithUsage> items = grouped.get(period);
                if (items == null || items.isEmpty()) continue;

                int days = PeriodKeyUtil.daysUntilReset(period);
                result.add(new ListItem(formatPeriodLabel(period), days));
                for (BenefitWithUsage bwu : items) {
                    result.add(new ListItem(bwu));
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
