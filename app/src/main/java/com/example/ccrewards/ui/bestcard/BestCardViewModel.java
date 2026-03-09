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
import com.example.ccrewards.data.model.RotationalBonus;
import com.example.ccrewards.data.model.RotationalBonusCategory;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.CustomCategoryRepository;
import com.example.ccrewards.data.repository.RewardRateRepository;
import com.example.ccrewards.data.repository.RotationalBonusRepository;
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

    /** Data for a single active quarterly/rotational bonus row in the banner. */
    public static class ActiveRotationalBonus {
        public final RotationalBonus bonus;
        public final String cardName;
        public final long cardColorPrimary;
        public final List<String> categoryLabels;  // formatted names for display

        public ActiveRotationalBonus(RotationalBonus bonus, String cardName,
                                     long cardColorPrimary, List<String> categoryLabels) {
            this.bonus = bonus;
            this.cardName = cardName;
            this.cardColorPrimary = cardColorPrimary;
            this.categoryLabels = categoryLabels;
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
    private final RotationalBonusRepository rotationalBonusRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Map<RewardCategory, List<BestCardForCategory>>> ranked =
            new MutableLiveData<>();
    private final MutableLiveData<List<CustomCategoryRanking>> customRanked =
            new MutableLiveData<>();
    private final MutableLiveData<List<ActiveWelcomeBonus>> activeBonuses =
            new MutableLiveData<>();
    private final MutableLiveData<List<ActiveRotationalBonus>> activeRotBonuses =
            new MutableLiveData<>();
    // Trigger recompute when rates, valuations, custom categories, or WB change
    private final MediatorLiveData<Void> trigger = new MediatorLiveData<>();

    @Inject
    public BestCardViewModel(CardRepository cardRepository, RewardRateRepository rateRepository,
                              CustomCategoryRepository customCategoryRepository,
                              WelcomeBonusRepository wbRepository,
                              RotationalBonusRepository rotationalBonusRepository) {
        this.cardRepository = cardRepository;
        this.rateRepository = rateRepository;
        this.customCategoryRepository = customCategoryRepository;
        this.wbRepository = wbRepository;
        this.rotationalBonusRepository = rotationalBonusRepository;

        trigger.addSource(rateRepository.getAllRates(), v -> recompute());
        trigger.addSource(rateRepository.getAllValuations(), v -> recompute());
        trigger.addSource(customCategoryRepository.getAllCustomCategoriesLive(), v -> recompute());
        trigger.addSource(wbRepository.getActiveLive(), v -> recompute());
        trigger.addSource(cardRepository.getActiveUserCards(), v -> recompute());
        trigger.addSource(rotationalBonusRepository.getActiveLive(), v -> recompute());
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

    public LiveData<List<ActiveRotationalBonus>> getActiveRotationalBonuses() {
        return activeRotBonuses;
    }

    public void updateRotationalBonusUsed(long bonusId, int usedCents, int limitCents) {
        rotationalBonusRepository.updateUsed(bonusId, usedCents, limitCents);
    }

    public void markRotationalBonusFullyUsed(long bonusId) {
        rotationalBonusRepository.markFullyUsed(bonusId);
    }

    public void deleteRotationalBonus(long bonusId) {
        rotationalBonusRepository.delete(bonusId);
    }

    public void insertRotationalBonus(RotationalBonus bonus,
                                      List<RotationalBonusCategory> cats,
                                      Runnable onComplete) {
        rotationalBonusRepository.insert(bonus, cats, onComplete);
    }

    public void markBonusAchieved(long userCardId) {
        executor.execute(() -> wbRepository.markAchieved(userCardId));
    }

    public void updateWelcomeBonusSpend(long userCardId, int spendUsedCents) {
        executor.execute(() -> wbRepository.updateSpendUsed(userCardId, spendUsedCents));
    }

    /** Exposed so BestCardFragment can trigger insertCategory with a callback. */
    public CustomCategoryRepository getCustomCategoryRepository() {
        return customCategoryRepository;
    }

    private void recompute() {
        executor.execute(() -> {
            // Load all data synchronously on background thread
            List<RewardRate> allRates = rateRepository.getAllRatesSync();
            List<PointValuation> valuations = rateRepository.getAllValuationsSync();
            List<CardDefinition> allCards = cardRepository.getAllCardDefinitionsSync();
            List<String> ownedIds = cardRepository.getActiveCardDefinitionIdsSync();
            List<CustomCategory> customCategories = customCategoryRepository.getAllCustomCategoriesSync();
            List<CustomCategoryRate> allCustomRates = customCategoryRepository.getAllRatesSync();
            List<UserCard> activeUserCards = cardRepository.getAllActiveUserCardsSync();

            if (allRates == null || valuations == null || allCards == null) return;

            // cardDefinitionId → all owned UserCard instances (for per-instance ranking)
            Map<String, List<UserCard>> userCardsByDef = new HashMap<>();
            if (activeUserCards != null) {
                for (UserCard uc : activeUserCards) {
                    userCardsByDef.computeIfAbsent(uc.cardDefinitionId, k -> new ArrayList<>()).add(uc);
                }
            }

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

            // Only show owned cards
            Set<String> eligibleIds = new HashSet<>(ownedIds);

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

            // ── Rotational bonus rates — keyed per user card, NOT injected into byCategory ──
            // Build userCardId → cardDefinitionId map
            Map<Long, String> userCardDefIdMap = new HashMap<>();
            for (UserCard uc : activeUserCards) {
                userCardDefIdMap.put(uc.id, uc.cardDefinitionId);
            }

            List<RotationalBonus> activeRbs = rotationalBonusRepository.getActiveSync();
            List<RotationalBonusCategory> allBonusCats = rotationalBonusRepository.getAllCategoriesSync();

            // Build: bonusId → list of categories
            Map<Long, List<RotationalBonusCategory>> catsByBonus = new HashMap<>();
            if (allBonusCats != null) {
                for (RotationalBonusCategory cat : allBonusCats) {
                    catsByBonus.computeIfAbsent(cat.rotationalBonusId, k -> new ArrayList<>()).add(cat);
                }
            }

            // Per-user-card rotational rates: userCardId → RewardCategory → rates
            // Applied individually during ranking build so two instances of the same card
            // definition get their own distinct effective rates.
            Map<Long, Map<RewardCategory, List<RewardRate>>> rotByUserCard = new HashMap<>();
            if (activeRbs != null) {
                for (RotationalBonus rb : activeRbs) {
                    String cardDefId = userCardDefIdMap.get(rb.userCardId);
                    if (cardDefId == null || !eligibleIds.contains(cardDefId)) continue;
                    List<RotationalBonusCategory> cats =
                            catsByBonus.getOrDefault(rb.id, new ArrayList<>());
                    for (RotationalBonusCategory cat : cats) {
                        try {
                            RewardCategory rc = RewardCategory.valueOf(cat.categoryName);
                            // The quarterly rate is the *total* earn rate for those categories.
                            // Subtract 1.0 so we add only the net additional on top of the
                            // card's base earn rate that is already included in stored rates.
                            double netBonus = Math.max(0, cat.rate - 1.0);
                            RewardRate synthRate = new RewardRate(
                                    cardDefId, rc, cat.rateType, netBonus, false, null, false);
                            if (cat.currencyName != null && !cat.currencyName.isEmpty()) {
                                synthRate.currencyName = cat.currencyName;
                            }
                            rotByUserCard
                                    .computeIfAbsent(rb.userCardId, k -> new HashMap<>())
                                    .computeIfAbsent(rc, k -> new ArrayList<>())
                                    .add(synthRate);
                        } catch (IllegalArgumentException ignored) {
                            // Free-text category — not a RewardCategory enum, skip
                        }
                    }
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
                    List<RewardRate> baseRates = entry.getValue();
                    CardDefinition cd = cardMap.get(cardId);
                    if (cd == null) continue;

                    List<UserCard> ownedInstances =
                            userCardsByDef.getOrDefault(cardId, new ArrayList<>());
                    if (ownedInstances.isEmpty()) {
                        // Not owned — one entry, base rates only, no rotational bonus
                        RateStats s = computeStats(baseRates, cd, cppMap);
                        rankings.add(new BestCardForCategory(
                                category, cardId, cd.displayName, s.primaryCurrency,
                                s.primaryType, s.primaryRate, s.totalEffective, false));
                    } else {
                        // One entry per owned user card instance, each with its own bonus rates
                        for (UserCard uc : ownedInstances) {
                            List<RewardRate> rates = new ArrayList<>(baseRates);
                            Map<RewardCategory, List<RewardRate>> ucRot = rotByUserCard.get(uc.id);
                            if (ucRot != null) {
                                List<RewardRate> catRot = ucRot.get(category);
                                if (catRot != null) rates.addAll(catRot);
                            }
                            RateStats s = computeStats(rates, cd, cppMap);
                            rankings.add(new BestCardForCategory(
                                    category, cardId,
                                    UserCard.label(cd.displayName, uc.lastFour, uc.nickname),
                                    s.primaryCurrency, s.primaryType,
                                    s.primaryRate, s.totalEffective, true));
                        }
                    }
                }

                rankings.sort((a, b) -> Double.compare(b.effectiveReturn, a.effectiveReturn));
                result.put(category, rankings);
            }

            ranked.postValue(result);

            // ── Custom category rankings ──────────────────────────────────────

            if (customCategories == null || customCategories.isEmpty()) {
                customRanked.postValue(new ArrayList<>());
            } else {
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
                            effectiveCurrency = (cr.currencyName != null && !cr.currencyName.isEmpty())
                                    ? cr.currencyName : cd.rewardCurrencyName;
                        } else {
                            RewardRate generalRate = generalRateMap.get(cardId);
                            if (generalRate == null) continue;
                            rate = generalRate.rate;
                            rateType = generalRate.rateType;
                            effectiveCurrency = cd.rewardCurrencyName;
                        }

                        double cpp = rateType == RateType.BILT_CASH
                                ? cppMap.getOrDefault("Bilt Cash", 1.0)
                                : cppMap.getOrDefault(effectiveCurrency, 1.0);
                        double effectiveReturn = rate * cpp;

                        List<UserCard> ownedInstances =
                                userCardsByDef.getOrDefault(cardId, new ArrayList<>());
                        if (ownedInstances.isEmpty()) {
                            rankings.add(new BestCardForCategory(
                                    RewardCategory.GENERAL, cardId, cd.displayName,
                                    effectiveCurrency, rateType, rate, effectiveReturn, false));
                        } else {
                            for (UserCard uc : ownedInstances) {
                                rankings.add(new BestCardForCategory(
                                        RewardCategory.GENERAL, cardId,
                                        UserCard.label(cd.displayName, uc.lastFour, uc.nickname),
                                        effectiveCurrency, rateType, rate, effectiveReturn, true));
                            }
                        }
                    }

                    rankings.sort((a, b) -> Double.compare(b.effectiveReturn, a.effectiveReturn));
                    customResult.add(new CustomCategoryRanking(cc, rankings));
                }

                customRanked.postValue(customResult);
            }

            // ── Active welcome bonus banner ────────────────────────────────────

            List<WelcomeBonus> wbs = wbRepository.getActiveSync();
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

                wbResult.add(new ActiveWelcomeBonus(wb,
                        UserCard.label(cd.displayName, uc.lastFour, uc.nickname),
                        cd.cardColorPrimary, bonusCashPct + basePct));
            }
            activeBonuses.postValue(wbResult);

            // ── Rotational bonus banner ─────────────────────────────────────────

            List<ActiveRotationalBonus> rotBonusResult = new ArrayList<>();
            if (activeRbs != null) {
                for (RotationalBonus rb : activeRbs) {
                    String cardDefId = userCardDefIdMap.get(rb.userCardId);
                    if (cardDefId == null) continue;
                    CardDefinition cd = cardMap.get(cardDefId);
                    if (cd == null) continue;
                    List<RotationalBonusCategory> cats =
                            catsByBonus.getOrDefault(rb.id, new ArrayList<>());
                    List<String> catLabels = new ArrayList<>();
                    for (RotationalBonusCategory cat : cats) {
                        catLabels.add(formatCategoryDisplayName(cat.categoryName));
                    }
                    UserCard rbUc = userCardMap.get(rb.userCardId);
                    rotBonusResult.add(new ActiveRotationalBonus(
                            rb, UserCard.label(cd.displayName, rbUc != null ? rbUc.lastFour : null, rbUc != null ? rbUc.nickname : null),
                            cd.cardColorPrimary, catLabels));
                }
            }
            activeRotBonuses.postValue(rotBonusResult);
        });
    }

    /** Formats a category name string for display in the rotational bonus banner. */
    private static String formatCategoryDisplayName(String name) {
        if (name == null) return "";
        try {
            RewardCategory rc = RewardCategory.valueOf(name);
            switch (rc) {
                case GENERAL:         return "General";
                case DINING:          return "Dining";
                case GROCERIES:       return "Groceries";
                case TRAVEL:          return "General Travel";
                case TRAVEL_PORTAL:   return "Travel Portal";
                case GAS:             return "Gas";
                case ENTERTAINMENT:   return "Entertainment";
                case ONLINE_SHOPPING:  return "Online Shopping";
                case ONLINE_GROCERIES: return "Online Groceries";
                case DRUGSTORES:       return "Drugstores";
                case TRANSIT:         return "Transit & Rideshare";
                case RENT_MORTGAGE:   return "Rent / Mortgage";
                case TRAVEL_HILTON:   return "Hilton";
                case TRAVEL_MARRIOTT: return "Marriott";
                case TRAVEL_IHG:      return "IHG";
                case TRAVEL_HYATT:    return "Hyatt";
                case TRAVEL_DELTA:    return "Delta";
                case TRAVEL_UNITED:   return "United";
                case TRAVEL_SOUTHWEST:return "Southwest";
                case TRAVEL_AA:       return "American Airlines";
                case TRAVEL_AEROPLAN: return "Aeroplan";
                case TRAVEL_BRITISH_AIRWAYS: return "British Airways";
                case TRAVEL_AER_LINGUS:  return "Aer Lingus";
                case TRAVEL_IBERIA:   return "Iberia";
                case TRAVEL_AIR_FRANCE_KLM: return "Air France / KLM";
                case TRAVEL_SPIRIT:   return "Spirit";
                case TRAVEL_ALLEGIANT:return "Allegiant";
                case TRAVEL_ALASKA:   return "Alaska Airlines";
                case TRAVEL_JETBLUE:  return "JetBlue";
                case TRAVEL_HAWAIIAN: return "Hawaiian Airlines";
                case TRAVEL_WYNDHAM:  return "Wyndham";
                case TRAVEL_FRONTIER: return "Frontier";
                case TRAVEL_LUFTHANSA:return "Lufthansa";
                case TRAVEL_EMIRATES: return "Emirates";
                case TRAVEL_CRUISES:  return "Cruises";
                default:              return name;
            }
        } catch (IllegalArgumentException e) {
            return name; // free-text category
        }
    }

    private static boolean isBrandSpecificTravel(RewardCategory cat) {
        switch (cat) {
            case TRAVEL_HILTON: case TRAVEL_MARRIOTT: case TRAVEL_IHG: case TRAVEL_HYATT:
            case TRAVEL_DELTA: case TRAVEL_UNITED: case TRAVEL_SOUTHWEST: case TRAVEL_AA:
            case TRAVEL_AEROPLAN: case TRAVEL_BRITISH_AIRWAYS: case TRAVEL_AER_LINGUS:
            case TRAVEL_IBERIA: case TRAVEL_AIR_FRANCE_KLM: case TRAVEL_SPIRIT:
            case TRAVEL_ALLEGIANT: case TRAVEL_ALASKA: case TRAVEL_JETBLUE:
            case TRAVEL_HAWAIIAN: case TRAVEL_WYNDHAM: case TRAVEL_FRONTIER:
            case TRAVEL_LUFTHANSA: case TRAVEL_EMIRATES: case TRAVEL_CRUISES:
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

    /** Aggregated rate statistics computed from a list of rates for one card+category. */
    private static class RateStats {
        double totalEffective;
        double primaryRate;
        RateType primaryType;
        String primaryCurrency;
    }

    /**
     * Computes effective return, primary rate type/currency, and summed primary rate
     * for a combined list of rates (base + any stacked rotational bonus rates).
     *
     * Primary type is determined by the rate contributing the highest effective return.
     * Primary rate is the SUM of all rates sharing that type+currency so that stacked
     * rates of the same type display correctly (e.g. 5x + 5x = "10x UR").
     */
    private static RateStats computeStats(List<RewardRate> rates, CardDefinition cd,
                                          Map<String, Double> cppMap) {
        RateStats s = new RateStats();
        s.primaryType = rates.get(0).rateType;
        s.primaryCurrency = cd.rewardCurrencyName;
        double maxEffective = 0.0;

        for (RewardRate r : rates) {
            String rateCurrency = (r.currencyName != null && !r.currencyName.isEmpty())
                    ? r.currencyName : cd.rewardCurrencyName;
            double cpp = r.rateType == RateType.BILT_CASH
                    ? cppMap.getOrDefault("Bilt Cash", 1.0)
                    : cppMap.getOrDefault(rateCurrency, 1.0);
            double effective = r.rate * cpp;
            s.totalEffective += effective;
            if (effective > maxEffective) {
                maxEffective = effective;
                s.primaryType = r.rateType;
                s.primaryCurrency = rateCurrency;
            }
        }

        // Sum all rates of the dominant type+currency (handles stacking: 5xUR + 5xUR = 10xUR)
        for (RewardRate r : rates) {
            String rateCurrency = (r.currencyName != null && !r.currencyName.isEmpty())
                    ? r.currencyName : cd.rewardCurrencyName;
            if (r.rateType == s.primaryType && rateCurrency.equals(s.primaryCurrency)) {
                s.primaryRate += r.rate;
            }
        }
        return s;
    }
}
