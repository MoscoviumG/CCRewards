package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.BenefitUsageWithAmount;
import com.example.ccrewards.data.model.ProductChangeRecord;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.WelcomeBonusRepository;
import com.example.ccrewards.util.PeriodKeyUtil;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CardDetailViewModel extends ViewModel {

    private final CardRepository cardRepository;
    private final WelcomeBonusRepository wbRepository;
    private final BenefitRepository benefitRepository;

    private final MutableLiveData<Long> userCardId = new MutableLiveData<>();

    // Switches source LiveData when userCardId changes
    private final MediatorLiveData<UserCardWithDetails> cardDetails = new MediatorLiveData<>();
    private final MediatorLiveData<List<ProductChangeRecord>> history = new MediatorLiveData<>();
    private final MediatorLiveData<WelcomeBonus> welcomeBonus = new MediatorLiveData<>();
    private final MediatorLiveData<Integer> creditUsageThisYear = new MediatorLiveData<>();

    private LiveData<UserCardWithDetails> detailsSource;
    private LiveData<List<ProductChangeRecord>> historySource;
    private LiveData<WelcomeBonus> wbSource;
    private LiveData<List<BenefitUsageWithAmount>> usageSource;

    @Inject
    public CardDetailViewModel(CardRepository cardRepository, WelcomeBonusRepository wbRepository,
                               BenefitRepository benefitRepository) {
        this.cardRepository = cardRepository;
        this.wbRepository = wbRepository;
        this.benefitRepository = benefitRepository;
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

    public void markWelcomeBonusAchieved(long userCardId) {
        wbRepository.markAchieved(userCardId);
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

    public void deleteCard(UserCard card) {
        cardRepository.deleteUserCard(card);
    }

    public void updateCard(UserCard card) {
        cardRepository.updateUserCard(card);
    }
}
