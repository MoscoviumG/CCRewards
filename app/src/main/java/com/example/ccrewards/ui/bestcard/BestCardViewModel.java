package com.example.ccrewards.ui.bestcard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.CustomCategory;
import com.example.ccrewards.data.model.CustomCategoryRate;
import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.data.model.relations.BestCardForCategory;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.CustomCategoryRepository;
import com.example.ccrewards.data.repository.RewardRateRepository;
import com.example.ccrewards.data.repository.WelcomeBonusRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BestCardViewModel extends ViewModel {

    public enum Filter { ALL_CARDS, MY_CARDS }

    /** Data for a single active welcome bonus row in the Best Card banner. */
    public static class ActiveWelcomeBonus {
        public final WelcomeBonus bonus;
        public final String cardName;
        public final long cardColorPrimary;
        public final double effectiveReturnPct; // bonus return + base GENERAL return, as percentage

        public ActiveWelcomeBonus(WelcomeBonus bonus, String cardName, long cardColorPrimary,
                                   double effectiveReturnPct) {
            this.bonus = bonus;
            this.cardName = cardName;
            this.cardColorPrimary = cardColorPrimary;
            this.effectiveReturnPct = effectiveReturnPct;
        }
    }

    /** Holds rankings for a single user-defined custom category. */
    public static class CustomCategoryRanking {
        public final CustomCategory category;
        public final List<BestCardForCategory> rankings;

        public CustomCategoryRanking(CustomCategory category, List<BestCardForCategory> rankings) {
            this.category = category;
            this.rankings = rankings;
        }
    }

    private final CardRepository cardRepository;
    private final RewardRateRepository rateRepository;
    private final CustomCategoryRepository customCategoryRepository;
    private final WelcomeBonusRepository wbRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Map<RewardCategory, List<BestCardForCategory>>> ranked =
            new MutableLiveData<>();
    private final MutableLiveData<List<CustomCategoryRanking>> customRanked =
            new MutableLiveData<>();
    private final MutableLiveData<List<ActiveWelcomeBonus>> activeBonuses =
            new MutableLiveData<>();
    private final MutableLiveData<Filter> filterMode = new MutableLiveData<>(Filter.MY_CARDS);

    // Trigger recompute when rates, valuations, custom categories, filter, or WB change
    private final MediatorLiveData<Void> trigger = new MediatorLiveData<>();

    @Inject
    public BestCardViewModel(CardRepository cardRepository, RewardRateRepository rateRepository,
                              CustomCategoryRepository customCategoryRepository,
                              WelcomeBonusRepository wbRepository) {
        this.cardRepository = cardRepository;
        this.rateRepository = rateRepository;
        this.customCategoryRepository = customCategoryRepository;
        this.wbRepository = wbRepository;

        trigger.addSource(rateRepository.getAllRates(), v -> recompute());
        trigger.addSource(rateRepository.getAllValuations(), v -> recompute());
        trigger.addSource(filterMode, v -> recompute());
        trigger.addSource(customCategoryRepository.getAllCustomCategoriesLive(), v -> recompute());
        trigger.addSource(wbRepository.getActiveLive(), v -> recompute());
        trigger.addSource(cardRepository.getActiveUserCards(), v -> recompute());
        trigger.observeForever(v -> {});
    }

    public LiveData<Map<RewardCategory, List<BestCardForCategory>>> getRanked() {
        return ranked;
    }

    public LiveData<List<CustomCategoryRanking>> getCustomRanked() {
        return customRanked;
    }

    public LiveData<List<ActiveWelcomeBonus>> getActiveBonuses() {
        return activeBonuses;
    }

    public void markBonusAchieved(long userCardId) {
        executor.execute(() -> wbRepository.markAchieved(userCardId));
    }

    public void setFilter(Filter filter) {
        filterMode.setValue(filter);
    }

    public Filter getCurrentFilter() {
        return filterMode.getValue() != null ? filterMode.getValue() : Filter.ALL_CARDS;
    }

    /** Exposed so BestCardFragment can trigger insertCategory with a callback. */
    public CustomCategoryRepository getCustomCategoryRepository() {
        return customCategoryRepository;
    }

    private void recompute() {
        executor.execute(() -> {
            Filter filter = filterMode.getValue() != null ? filterMode.getValue() : Filter.ALL_CARDS;

            // Load all data synchronously on background thread
            List<RewardRate> allRates = rateRepository.getAllRatesSync();
            List<PointValuation> valuations = rateRepository.getAllValuationsSync();
            List<CardDefinition> allCards = cardRepository.getAllCardDefinitionsSync();
            List<String> ownedIds = cardRepository.getActiveCardDefinitionIdsSync();
            List<CustomCategory> customCategories = customCategoryRepository.getAllCustomCategoriesSync();
            List<CustomCategoryRate> allCustomRates = customCategoryRepository.getAllRatesSync();

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
            Set<String> eligibleIds = new HashSet<>();
            for (CardDefinition cd : allCards) {
                if (filter == Filter.MY_CARDS) {
                    if (ownedIds.contains(cd.id)) eligibleIds.add(cd.id);
                } else {
                    eligibleIds.add(cd.id);
                }
            }

            // ── Built-in category rankings ─────────────────────────────────────

            // Group rates by category → cardDefinitionId → list of rates
            Map<RewardCategory, Map<String, List<RewardRate>>> byCategory = new HashMap<>();
            for (RewardRate rate : allRates) {
                if (!eligibleIds.contains(rate.cardDefinitionId)) continue;
                byCategory
                        .computeIfAbsent(rate.category, k -> new HashMap<>())
                        .computeIfAbsent(rate.cardDefinitionId, k -> new ArrayList<>())
                        .add(rate);
            }

            // Also build a GENERAL rate lookup for fallback: cardId → RewardRate
            Map<String, RewardRate> generalRateMap = new HashMap<>();
            for (RewardRate rate : allRates) {
                if (rate.category == RewardCategory.GENERAL && !rate.isCustomized) {
                    // Keep the highest GENERAL rate per card (in case of multiple)
                    RewardRate existing = generalRateMap.get(rate.cardDefinitionId);
                    if (existing == null || rate.rate > existing.rate) {
                        generalRateMap.put(rate.cardDefinitionId, rate);
                    }
                }
            }

            // Inject GENERAL-rate fallback for built-in categories.
            // Any eligible card that has a GENERAL rate but NO explicit rate for a specific
            // category should still appear in that category tile (e.g. Freedom Unlimited 1.5x
            // showing up in the Rent/Mortgage tile).
            // Brand-specific travel sub-categories are excluded — only cards with an explicit
            // rate for that brand should appear there.
            for (RewardCategory cat : RewardCategory.values()) {
                if (cat == RewardCategory.GENERAL) continue;
                if (isBrandSpecificTravel(cat)) continue;
                Map<String, List<RewardRate>> cardsInCat =
                        byCategory.computeIfAbsent(cat, k -> new HashMap<>());
                for (Map.Entry<String, RewardRate> e : generalRateMap.entrySet()) {
                    String cardId = e.getKey();
                    if (!eligibleIds.contains(cardId)) continue;
                    if (cardsInCat.containsKey(cardId)) continue; // already has explicit rate
                    List<RewardRate> synth = new ArrayList<>();
                    synth.add(e.getValue());
                    cardsInCat.put(cardId, synth);
                }
            }

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

                    double totalEffectiveReturn = 0.0;
                    double primaryRate = 0.0;
                    RateType primaryType = rates.get(0).rateType;
                    String primaryCurrency = cd.rewardCurrencyName;

                    for (RewardRate r : rates) {
                        // Use per-rate currency if set, otherwise fall back to card default
                        String rateCurrency = (r.currencyName != null && !r.currencyName.isEmpty())
                                ? r.currencyName : cd.rewardCurrencyName;
                        double cpp;
                        if (r.rateType == RateType.BILT_CASH) {
                            cpp = cppMap.getOrDefault("Bilt Cash", 1.0);
                        } else {
                            cpp = cppMap.getOrDefault(rateCurrency, 1.0);
                        }
                        double effectiveReturn = r.rate * cpp / 100.0;
                        totalEffectiveReturn += effectiveReturn;

                        if (r.rate > primaryRate) {
                            primaryRate = r.rate;
                            primaryType = r.rateType;
                            primaryCurrency = rateCurrency;
                        }
                    }

                    rankings.add(new BestCardForCategory(
                            category, cardId, cd.displayName, primaryCurrency,
                            primaryType, primaryRate, totalEffectiveReturn,
                            ownedIds.contains(cardId)));
                }

                rankings.sort((a, b) -> Double.compare(b.effectiveReturn, a.effectiveReturn));
                result.put(category, rankings);
            }

            ranked.postValue(result);

            // ── Custom category rankings ──────────────────────────────────────

            if (customCategories == null || customCategories.isEmpty()) {
                customRanked.postValue(new ArrayList<>());
                return;
            }

            // Build lookup: customCategoryId → (cardId → CustomCategoryRate)
            Map<Long, Map<String, CustomCategoryRate>> customRatesByCat = new HashMap<>();
            if (allCustomRates != null) {
                for (CustomCategoryRate cr : allCustomRates) {
                    customRatesByCat
                            .computeIfAbsent(cr.customCategoryId, k -> new HashMap<>())
                            .put(cr.cardDefinitionId, cr);
                }
            }

            List<CustomCategoryRanking> customResult = new ArrayList<>();
            for (CustomCategory cc : customCategories) {
                Map<String, CustomCategoryRate> overrides =
                        customRatesByCat.getOrDefault(cc.id, new HashMap<>());

                List<BestCardForCategory> rankings = new ArrayList<>();
                for (String cardId : eligibleIds) {
                    CardDefinition cd = cardMap.get(cardId);
                    if (cd == null) continue;

                    double rate;
                    RateType rateType;
                    String effectiveCurrency;

                    if (overrides.containsKey(cardId)) {
                        CustomCategoryRate cr = overrides.get(cardId);
                        rate = cr.rate;
                        rateType = cr.rateType;
                        // Use the custom rate's specified currency; fall back to card's default
                        effectiveCurrency = (cr.currencyName != null && !cr.currencyName.isEmpty())
                                ? cr.currencyName : cd.rewardCurrencyName;
                    } else {
                        // Fallback to GENERAL rate
                        RewardRate generalRate = generalRateMap.get(cardId);
                        if (generalRate == null) continue;
                        rate = generalRate.rate;
                        rateType = generalRate.rateType;
                        effectiveCurrency = cd.rewardCurrencyName;
                    }

                    double cpp = cppMap.getOrDefault(effectiveCurrency, 1.0);
                    if (rateType == RateType.BILT_CASH) {
                        cpp = cppMap.getOrDefault("Bilt Cash", 1.0);
                    }
                    double effectiveReturn = rate * cpp / 100.0;

                    // Use GENERAL as a placeholder RewardCategory for the ranking entry
                    rankings.add(new BestCardForCategory(
                            RewardCategory.GENERAL, cardId, cd.displayName, cd.rewardCurrencyName,
                            rateType, rate, effectiveReturn, ownedIds.contains(cardId)));
                }

                rankings.sort((a, b) -> Double.compare(b.effectiveReturn, a.effectiveReturn));
                customResult.add(new CustomCategoryRanking(cc, rankings));
            }

            customRanked.postValue(customResult);

            // ── Active welcome bonus banner ────────────────────────────────────

            List<WelcomeBonus> wbs = wbRepository.getActiveSync();
            List<UserCard> activeUserCards = cardRepository.getAllActiveUserCardsSync();
            Map<Long, UserCard> userCardMap = new HashMap<>();
            for (UserCard uc : activeUserCards) userCardMap.put(uc.id, uc);

            LocalDate today = LocalDate.now();
            List<ActiveWelcomeBonus> wbResult = new ArrayList<>();
            for (WelcomeBonus wb : wbs) {
                if (wb.deadline != null && wb.deadline.isBefore(today)) continue; // expired
                UserCard uc = userCardMap.get(wb.userCardId);
                if (uc == null) continue;
                CardDefinition cd = cardMap.get(uc.cardDefinitionId);
                if (cd == null) continue;

                double cpp = cppMap.getOrDefault(wb.bonusCurrencyName, 1.0);
                double bonusRatePerDollar = wb.bonusPoints / (wb.spendRequirementCents / 100.0);
                double bonusCashPct = bonusRatePerDollar * cpp / 100.0 * 100.0;

                // Add base GENERAL rate for context
                RewardRate generalRate = generalRateMap.get(cd.id);
                double basePct = generalRate != null
                        ? generalRate.rate * cppMap.getOrDefault(cd.rewardCurrencyName, 1.0) / 100.0 * 100.0
                        : 0;

                wbResult.add(new ActiveWelcomeBonus(wb, cd.displayName,
                        cd.cardColorPrimary, bonusCashPct + basePct));
            }
            activeBonuses.postValue(wbResult);
        });
    }

    private static boolean isBrandSpecificTravel(RewardCategory cat) {
        switch (cat) {
            case TRAVEL_HILTON: case TRAVEL_MARRIOTT: case TRAVEL_IHG: case TRAVEL_HYATT:
            case TRAVEL_DELTA: case TRAVEL_UNITED: case TRAVEL_SOUTHWEST: case TRAVEL_AA:
            case TRAVEL_AEROPLAN: case TRAVEL_BRITISH_AIRWAYS: case TRAVEL_AER_LINGUS:
            case TRAVEL_IBERIA: case TRAVEL_AIR_FRANCE_KLM: case TRAVEL_SPIRIT:
            case TRAVEL_ALLEGIANT: case TRAVEL_ALASKA: case TRAVEL_CRUISES:
                return true;
            default:
                return false;
        }
    }

    /** Returns the top-ranked card for a given built-in category, or null if none. */
    public static BestCardForCategory getTop(Map<RewardCategory, List<BestCardForCategory>> ranked,
                                              RewardCategory category) {
        List<BestCardForCategory> list = ranked.get(category);
        return (list != null && !list.isEmpty()) ? list.get(0) : null;
    }
}
