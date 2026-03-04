package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CustomCategory;
import com.example.ccrewards.data.model.CustomCategoryRate;
import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.CustomCategoryRepository;
import com.example.ccrewards.data.repository.RewardRateRepository;
import com.example.ccrewards.data.seed.SeedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditRewardRatesViewModel extends ViewModel {

    private final RewardRateRepository rateRepository;
    private final CustomCategoryRepository customCategoryRepository;
    private final CardRepository cardRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<RewardRate>> rates = new MutableLiveData<>();
    private String cardDefinitionId;

    @Inject
    public EditRewardRatesViewModel(RewardRateRepository rateRepository,
                                     CustomCategoryRepository customCategoryRepository,
                                     CardRepository cardRepository) {
        this.rateRepository = rateRepository;
        this.customCategoryRepository = customCategoryRepository;
        this.cardRepository = cardRepository;
    }

    public void loadRates(String cardDefinitionId) {
        this.cardDefinitionId = cardDefinitionId;
        rateRepository.getRatesForCard(cardDefinitionId).observeForever(rates::setValue);
    }

    public LiveData<List<RewardRate>> getRates() {
        return rates;
    }

    public LiveData<List<CustomCategory>> getCustomCategories() {
        return customCategoryRepository.getAllCustomCategoriesLive();
    }

    /** Sync — call from background thread only. */
    public List<CustomCategoryRate> getCustomRatesForCardSync() {
        if (cardDefinitionId == null) return Collections.emptyList();
        return customCategoryRepository.getRatesForCardSync(cardDefinitionId);
    }

    /** Sync — call from background thread only. */
    public List<CustomCategory> getAllCustomCategoriesSync() {
        return customCategoryRepository.getAllCustomCategoriesSync();
    }

    /** Sync — call from background thread only. Returns the card's rewardCurrencyName, or null. */
    public String getCardCurrencySync() {
        if (cardDefinitionId == null) return null;
        com.example.ccrewards.data.model.CardDefinition def =
                cardRepository.getCardDefinitionSync(cardDefinitionId);
        return def != null ? def.rewardCurrencyName : null;
    }

    /** Sync — call from background thread only. */
    public List<PointValuation> getAllValuationsSync() {
        return rateRepository.getAllValuationsSync();
    }

    public void insertValuationSync(PointValuation pv, Runnable onDone) {
        executor.execute(() -> {
            rateRepository.insertValuationSync(pv);
            if (onDone != null) onDone.run();
        });
    }

    // ── Standard RewardRate operations ───────────────────────────────────────

    public void saveRate(RewardRate rate) {
        rate.isCustomized = true;
        rateRepository.updateRate(rate);
    }

    public void insertStandardRate(RewardCategory category, double rate, RateType type,
                                    String currencyName) {
        RewardRate r = new RewardRate(cardDefinitionId, category, type, rate);
        r.isCustomized = true;
        r.currencyName = currencyName;
        rateRepository.insertRate(r);
    }

    public void deleteStandardRate(RewardRate rate) {
        rateRepository.deleteRate(rate);
    }

    // ── CustomCategoryRate operations ─────────────────────────────────────────

    public void saveCustomRate(CustomCategoryRate rate) {
        rate.cardDefinitionId = cardDefinitionId;
        executor.execute(() -> customCategoryRepository.upsertRateSync(rate));
    }

    public void deleteCustomRate(CustomCategoryRate rate) {
        executor.execute(() -> customCategoryRepository.deleteRateForCardSync(
                rate.customCategoryId, rate.cardDefinitionId));
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    /**
     * Full reset: deletes ALL reward_rates for this card, re-inserts seed defaults,
     * and clears all custom-category rates for this card. Calls onDone on the background
     * thread when finished — caller should marshal back to main thread if needed.
     */
    public void resetAllCustomizations(Runnable onDone) {
        if (cardDefinitionId == null) {
            if (onDone != null) onDone.run();
            return;
        }
        final String id = cardDefinitionId;
        executor.execute(() -> {
            // Gather seed rates for this card
            List<RewardRate> allSeed = SeedData.getRewardRates();
            List<RewardRate> cardSeed = new ArrayList<>();
            for (RewardRate r : allSeed) {
                if (id.equals(r.cardDefinitionId)) cardSeed.add(r);
            }
            rateRepository.fullResetCardSync(id, cardSeed);
            customCategoryRepository.deleteAllRatesForCardSync(id);
            if (onDone != null) onDone.run();
        });
    }

    public String getCardDefinitionId() {
        return cardDefinitionId;
    }
}
