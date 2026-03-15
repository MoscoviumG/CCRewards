package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.BenefitUsageWithAmount;
import com.example.ccrewards.data.model.FreeNightAward;
import com.example.ccrewards.data.model.ProductChangeRecord;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.model.RotationalBonus;
import com.example.ccrewards.data.model.RotationalBonusCategory;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.data.model.FreeNightAward;
import com.example.ccrewards.data.model.FreeNightValuation;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.FreeNightRepository;
import com.example.ccrewards.data.repository.RotationalBonusRepository;
import com.example.ccrewards.data.repository.WelcomeBonusRepository;
import com.example.ccrewards.util.PeriodKeyUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CardDetailViewModel extends ViewModel {

    /** Combined bonus + display-ready category string for the card detail list. */
    public static class RotationalBonusInfo {
        public final RotationalBonus bonus;
        public final String categoryDisplay; // e.g. "Dining · Groceries"

        RotationalBonusInfo(RotationalBonus bonus, String categoryDisplay) {
            this.bonus = bonus;
            this.categoryDisplay = categoryDisplay;
        }
    }

    /** Combined award + resolved valuation label for display. */
    public static class FreeNightInfo {
        public final FreeNightAward award;
        public final String typeLabel; // e.g. "Marriott Free Night (35k)"
        public final int valueCents;   // user's valuation for this type

        FreeNightInfo(FreeNightAward award, String typeLabel, int valueCents) {
            this.award = award;
            this.typeLabel = typeLabel;
            this.valueCents = valueCents;
        }
    }

    private final CardRepository cardRepository;
    private final WelcomeBonusRepository wbRepository;
    private final BenefitRepository benefitRepository;
    private final RotationalBonusRepository rotRepository;
    private final FreeNightRepository freeNightRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Long> userCardId = new MutableLiveData<>();

    // Switches source LiveData when userCardId changes
    private final MediatorLiveData<UserCardWithDetails> cardDetails = new MediatorLiveData<>();
    private final MediatorLiveData<List<ProductChangeRecord>> history = new MediatorLiveData<>();
    private final MediatorLiveData<WelcomeBonus> welcomeBonus = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> creditUsageThisYear = new MediatorLiveData<>();
    private final MediatorLiveData<List<RotationalBonusInfo>> rotBonuses = new MediatorLiveData<>();
    private final MediatorLiveData<List<FreeNightInfo>> freeNights = new MediatorLiveData<>();

    private LiveData<UserCardWithDetails> detailsSource;
    private LiveData<List<ProductChangeRecord>> historySource;
    private LiveData<WelcomeBonus> wbSource;
    private LiveData<List<BenefitUsageWithAmount>> usageSource;
    private LiveData<List<RotationalBonus>> rbSource;
    private LiveData<List<FreeNightAward>> fnSource;

    @Inject
    public CardDetailViewModel(CardRepository cardRepository, WelcomeBonusRepository wbRepository,
                               BenefitRepository benefitRepository,
                               RotationalBonusRepository rotRepository,
                               FreeNightRepository freeNightRepository) {
        this.cardRepository = cardRepository;
        this.wbRepository = wbRepository;
        this.benefitRepository = benefitRepository;
        this.rotRepository = rotRepository;
        this.freeNightRepository = freeNightRepository;
    }

    public void loadCard(long id) {
        userCardId.setValue(id);

        // Remove old sources from creditUsageThisYear before swapping detailsSource
        if (detailsSource != null) creditUsageThisYear.removeSource(detailsSource);
        if (usageSource != null) creditUsageThisYear.removeSource(usageSource);

        // Swap LiveData sources
        if (detailsSource != null) cardDetails.removeSource(detailsSource);
        detailsSource = cardRepository.getUserCardWithDetails(id);
        cardDetails.addSource(detailsSource, cardDetails::setValue);

        if (historySource != null) history.removeSource(historySource);
        historySource = cardRepository.getProductChangeHistory(id);
        history.addSource(historySource, history::setValue);

        if (wbSource != null) welcomeBonus.removeSource(wbSource);
        wbSource = wbRepository.getLiveForCard(id);
        welcomeBonus.addSource(wbSource, welcomeBonus::setValue);

        // Credit usage for this card
        usageSource = benefitRepository.getUsedWithAmountsForCard(id);
        creditUsageThisYear.addSource(detailsSource,
                details -> recomputeCreditUsage(details, usageSource.getValue()));
        creditUsageThisYear.addSource(usageSource,
                usages -> recomputeCreditUsage(cardDetails.getValue(), usages));

        // Quarterly bonuses for this card
        if (rbSource != null) rotBonuses.removeSource(rbSource);
        rbSource = rotRepository.getForUserCard(id);
        final long cardIdFinal = id;
        rotBonuses.addSource(rbSource, bonusList ->
                executor.execute(() -> {
                    List<RotationalBonusCategory> cats =
                            rotRepository.getCategoriesForUserCardSync(cardIdFinal);
                    Map<Long, List<String>> labelMap = new HashMap<>();
                    for (RotationalBonusCategory c : cats) {
                        labelMap.computeIfAbsent(c.rotationalBonusId, k -> new ArrayList<>())
                                .add(formatCategoryName(c.categoryName));
                    }
                    List<RotationalBonusInfo> result = new ArrayList<>();
                    if (bonusList != null) {
                        for (RotationalBonus rb : bonusList) {
                            List<String> labels = labelMap.getOrDefault(rb.id, new ArrayList<>());
                            result.add(new RotationalBonusInfo(rb, String.join(" · ", labels)));
                        }
                    }
                    rotBonuses.postValue(result);
                }));

        // Free night awards for this card
        if (fnSource != null) freeNights.removeSource(fnSource);
        fnSource = freeNightRepository.getAwardsForCard(id);
        freeNights.addSource(fnSource, awardList ->
                executor.execute(() -> {
                    // Migrate any legacy multi-count awards to individual records
                    freeNightRepository.splitMultiCountAwardsSync(id);

                    List<FreeNightInfo> result = new ArrayList<>();
                    if (awardList != null) {
                        // Count WB awards per typeKey so we can number them
                        Map<String, Integer> wbCountByType = new HashMap<>();
                        for (FreeNightAward award : awardList) {
                            if (award.isFromWelcomeBonus) {
                                wbCountByType.merge(award.typeKey, 1, Integer::sum);
                            }
                        }
                        Map<String, Integer> wbIndexByType = new HashMap<>();
                        for (FreeNightAward award : awardList) {
                            FreeNightValuation val =
                                    freeNightRepository.getValuationSync(award.typeKey);
                            // Prefer award.label (set for recurring/annual), else valuation label
                            String baseLabel = award.label != null ? award.label
                                    : (val != null ? val.label : award.typeKey);
                            String displayLabel;
                            if (award.isFromWelcomeBonus
                                    && wbCountByType.getOrDefault(award.typeKey, 1) > 1) {
                                int idx = wbIndexByType.getOrDefault(award.typeKey, 0) + 1;
                                wbIndexByType.put(award.typeKey, idx);
                                displayLabel = baseLabel + " · FN " + idx;
                            } else {
                                displayLabel = baseLabel;
                            }
                            int valueCents = val != null ? val.valueCents : 0;
                            result.add(new FreeNightInfo(award, displayLabel, valueCents));
                        }
                    }
                    freeNights.postValue(result);
                }));
    }

    public LiveData<UserCardWithDetails> getCardDetails() {
        return cardDetails;
    }

    public LiveData<List<ProductChangeRecord>> getHistory() {
        return history;
    }

    public LiveData<WelcomeBonus> getWelcomeBonus() {
        return welcomeBonus;
    }

    public LiveData<Integer> getCreditUsageThisYear() {
        return creditUsageThisYear;
    }

    public LiveData<List<RotationalBonusInfo>> getRotationalBonuses() {
        return rotBonuses;
    }

    public LiveData<List<FreeNightInfo>> getFreeNights() {
        return freeNights;
    }

    public void markFreeNightUsed(long awardId, int newUsedCount) {
        freeNightRepository.markUsed(awardId, newUsedCount);
    }

    public void deleteFreeNight(long awardId) {
        freeNightRepository.deleteAward(awardId);
    }

    public void addFreeNightFromWelcomeBonus(long cardId, String typeKey, int count) {
        if (typeKey == null || typeKey.isEmpty()) return;
        FreeNightAward award = new FreeNightAward(
                cardId, typeKey, null, null, count, true);
        freeNightRepository.insertAward(award, null);
    }

    public void deleteRotationalBonus(long bonusId) {
        rotRepository.delete(bonusId);
    }

    public void updateRotationalBonusUsed(long bonusId, int usedCents, int limitCents) {
        rotRepository.updateUsed(bonusId, usedCents, limitCents);
    }

    public void markRotationalBonusFullyUsed(long bonusId) {
        rotRepository.markFullyUsed(bonusId);
    }

    private void recomputeCreditUsage(UserCardWithDetails details,
                                       List<BenefitUsageWithAmount> usages) {
        if (details == null || usages == null) { creditUsageThisYear.setValue(null); return; }
        LocalDate openDate = details.userCard.openDate;
        if (openDate == null) { creditUsageThisYear.setValue(null); return; }
        LocalDate today = LocalDate.now();
        LocalDate lastAnn = openDate.withYear(today.getYear());
        if (lastAnn.isAfter(today)) lastAnn = lastAnn.minusYears(1);
        LocalDate nextAnn = lastAnn.plusYears(1);
        int total = 0;
        for (BenefitUsageWithAmount u : usages) {
            LocalDate ps = PeriodKeyUtil.periodKeyStartDate(u.periodKey);
            if (ps != null && !ps.isBefore(lastAnn) && ps.isBefore(nextAnn)) total += u.amountCents;
        }
        creditUsageThisYear.setValue(total > 0 ? total : null);
    }

    public void upsertWelcomeBonus(WelcomeBonus wb) {
        wbRepository.upsert(wb);
    }

    public void deleteWelcomeBonus(WelcomeBonus wb) {
        wbRepository.delete(wb);
    }

    public void markWelcomeBonusAchieved(WelcomeBonus wb) {
        wbRepository.markAchieved(wb.userCardId);
        if (wb.fnTypeKey != null && !wb.fnTypeKey.isEmpty()) {
            int count = Math.max(1, wb.fnCount);
            for (int i = 0; i < count; i++) {
                FreeNightAward award = new FreeNightAward(
                        wb.userCardId, wb.fnTypeKey, null, null, 1, true);
                freeNightRepository.insertAward(award, null);
            }
        }
    }

    /**
     * Returns rates grouped by category name for display in the detail screen.
     * Categories with multiple rate types (e.g. Bilt dual-currency) are merged into
     * a single display string.
     */
    public static Map<String, String> buildRateDisplay(List<RewardRate> rates) {
        Map<String, StringBuilder> map = new LinkedHashMap<>();
        for (RewardRate rate : rates) {
            String cat = formatCategory(rate.category.name());
            StringBuilder sb = map.computeIfAbsent(cat, k -> new StringBuilder());
            if (sb.length() > 0) sb.append(" + ");
            sb.append(formatRateRow(rate));
        }
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, StringBuilder> entry : map.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toString());
        }
        return result;
    }

    private static String formatCategory(String enumName) {
        // Convert "RENT_MORTGAGE" → "Rent / Mortgage"
        String[] parts = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append(part.charAt(0)).append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private static String formatRateRow(RewardRate rate) {
        switch (rate.rateType) {
            case CASHBACK:
                return String.format("%.1f%%", rate.rate);
            case BILT_CASH:
                return String.format("%.0f%% Bilt Cash", rate.rate);
            case MILES:
                if (rate.rate == Math.floor(rate.rate)) {
                    return (int) rate.rate + "x Miles";
                }
                return String.format("%.1fx Miles", rate.rate);
            case POINTS:
            default:
                if (rate.rate == Math.floor(rate.rate)) {
                    return (int) rate.rate + "x Points";
                }
                return String.format("%.1fx Points", rate.rate);
        }
    }

    private static String formatCategoryName(String name) {
        if (name == null) return "";
        try {
            RewardCategory rc = RewardCategory.valueOf(name);
            switch (rc) {
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
                case TRAVEL_ALASKA:   return "Alaska Airlines";
                case TRAVEL_JETBLUE:  return "JetBlue";
                case TRAVEL_HAWAIIAN: return "Hawaiian Airlines";
                case TRAVEL_WYNDHAM:  return "Wyndham";
                case TRAVEL_FRONTIER: return "Frontier";
                case TRAVEL_LUFTHANSA:return "Lufthansa";
                case TRAVEL_EMIRATES: return "Emirates";
                default:              break;
            }
        } catch (IllegalArgumentException ignored) {}
        return name; // free-text custom category
    }

    public void deleteCard(UserCard card) {
        cardRepository.deleteUserCard(card);
    }

    public void updateCard(UserCard card) {
        cardRepository.updateUserCard(card);
    }

    public void setCloseDate(long userCardId, LocalDate closeDate) {
        cardRepository.setCloseDate(userCardId, closeDate);
        String note = closeDate != null ? "Account closed" : "Account reopened";
        LocalDate eventDate = closeDate != null ? closeDate : LocalDate.now();
        cardRepository.insertProductChangeRecord(
                new ProductChangeRecord(userCardId, null, null, eventDate, note));
    }

    public void deleteProductChangeRecord(ProductChangeRecord record) {
        cardRepository.deleteProductChangeRecord(record);
    }

    public void updateProductChangeRecord(ProductChangeRecord record) {
        cardRepository.updateProductChangeRecord(record);
    }
}
