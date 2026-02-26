package com.example.ccrewards.ui.bestcard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.model.relations.BestCardForCategory;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.RewardRateRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BestCardViewModel extends ViewModel {

    public enum Filter { ALL_CARDS, MY_CARDS, PERSONAL, BUSINESS }

    private final CardRepository cardRepository;
    private final RewardRateRepository rateRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Map<RewardCategory, List<BestCardForCategory>>> ranked =
            new MutableLiveData<>();
    private final MutableLiveData<Filter> filterMode = new MutableLiveData<>(Filter.ALL_CARDS);

    // Trigger recompute when rates or valuations change
    private final MediatorLiveData<Void> trigger = new MediatorLiveData<>();

    @Inject
    public BestCardViewModel(CardRepository cardRepository, RewardRateRepository rateRepository) {
        this.cardRepository = cardRepository;
        this.rateRepository = rateRepository;

        // Observe both rates and valuations; recompute on any change
        trigger.addSource(rateRepository.getAllRates(), v -> recompute());
        trigger.addSource(rateRepository.getAllValuations(), v -> recompute());
        trigger.addSource(filterMode, v -> recompute());
        trigger.observeForever(v -> {});
    }

    public LiveData<Map<RewardCategory, List<BestCardForCategory>>> getRanked() {
        return ranked;
    }

    public void setFilter(Filter filter) {
        filterMode.setValue(filter);
    }

    public Filter getCurrentFilter() {
        return filterMode.getValue() != null ? filterMode.getValue() : Filter.ALL_CARDS;
    }

    private void recompute() {
        executor.execute(() -> {
            Filter filter = filterMode.getValue() != null ? filterMode.getValue() : Filter.ALL_CARDS;

            // Load all data synchronously on background thread
            List<RewardRate> allRates = rateRepository.getAllRatesSync();
            List<PointValuation> valuations = rateRepository.getAllValuationsSync();
            List<CardDefinition> allCards = cardRepository.getAllCardDefinitionsSync();
            List<String> ownedIds = cardRepository.getActiveCardDefinitionIdsSync();

            if (allRates == null || valuations == null || allCards == null) return;

            // Build centsPerPoint lookup: currencyName → cpp
            Map<String, Double> cppMap = new HashMap<>();
            for (PointValuation v : valuations) {
                cppMap.put(v.rewardCurrencyName, v.centsPerPoint);
            }

            // Build card lookup: cardDefinitionId → CardDefinition
            Map<String, CardDefinition> cardMap = new HashMap<>();
            for (CardDefinition cd : allCards) {
                cardMap.put(cd.id, cd);
            }

            // Apply filter: determine which cardDefinitionIds are eligible
            java.util.Set<String> eligibleIds = new java.util.HashSet<>();
            for (CardDefinition cd : allCards) {
                switch (filter) {
                    case MY_CARDS:
                        if (ownedIds.contains(cd.id)) eligibleIds.add(cd.id);
                        break;
                    case PERSONAL:
                        if (!cd.isBusinessCard) eligibleIds.add(cd.id);
                        break;
                    case BUSINESS:
                        if (cd.isBusinessCard) eligibleIds.add(cd.id);
                        break;
                    case ALL_CARDS:
                    default:
                        eligibleIds.add(cd.id);
                        break;
                }
            }

            // Group rates by category → cardDefinitionId → list of rates
            Map<RewardCategory, Map<String, List<RewardRate>>> byCategory = new HashMap<>();
            for (RewardRate rate : allRates) {
                if (!eligibleIds.contains(rate.cardDefinitionId)) continue;
                byCategory
                        .computeIfAbsent(rate.category, k -> new HashMap<>())
                        .computeIfAbsent(rate.cardDefinitionId, k -> new ArrayList<>())
                        .add(rate);
            }

            // For each category, compute total effective return per card and sort
            Map<RewardCategory, List<BestCardForCategory>> result = new HashMap<>();

            for (RewardCategory category : RewardCategory.values()) {
                Map<String, List<RewardRate>> cardsInCategory = byCategory.get(category);
                if (cardsInCategory == null || cardsInCategory.isEmpty()) {
                    result.put(category, new ArrayList<>());
                    continue;
                }

                List<BestCardForCategory> rankings = new ArrayList<>();
                for (Map.Entry<String, List<RewardRate>> entry : cardsInCategory.entrySet()) {
                    String cardId = entry.getKey();
                    List<RewardRate> rates = entry.getValue();
                    CardDefinition cd = cardMap.get(cardId);
                    if (cd == null) continue;

                    // Sum effective returns across all rate rows for this card+category
                    double totalEffectiveReturn = 0.0;
                    double primaryRate = 0.0;
                    com.example.ccrewards.data.model.RateType primaryType = rates.get(0).rateType;

                    for (RewardRate r : rates) {
                        double cpp = cppMap.getOrDefault(cd.rewardCurrencyName, 1.0);
                        // For BILT_CASH specifically, always use 1.0¢ (it's a fixed value)
                        if (r.rateType == com.example.ccrewards.data.model.RateType.BILT_CASH) {
                            cpp = cppMap.getOrDefault("Bilt Cash", 1.0);
                        }
                        double effectiveReturn = r.rate * cpp / 100.0;
                        totalEffectiveReturn += effectiveReturn;

                        // Track the primary rate (highest-value rate type for display)
                        if (r.rate > primaryRate) {
                            primaryRate = r.rate;
                            primaryType = r.rateType;
                        }
                    }

                    rankings.add(new BestCardForCategory(
                            category, cardId, cd.displayName, cd.rewardCurrencyName,
                            primaryType, primaryRate, totalEffectiveReturn,
                            ownedIds.contains(cardId)));
                }

                // Sort by effective return descending
                rankings.sort((a, b) -> Double.compare(b.effectiveReturn, a.effectiveReturn));
                result.put(category, rankings);
            }

            ranked.postValue(result);
        });
    }

    /** Returns the top-ranked card for a given category, or null if none. */
    public static BestCardForCategory getTop(Map<RewardCategory, List<BestCardForCategory>> ranked,
                                              RewardCategory category) {
        List<BestCardForCategory> list = ranked.get(category);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }
}
