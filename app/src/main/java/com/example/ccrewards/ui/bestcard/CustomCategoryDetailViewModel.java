package com.example.ccrewards.ui.bestcard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.CustomCategory;
import com.example.ccrewards.data.model.CustomCategoryRate;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.CustomCategoryRepository;
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
public class CustomCategoryDetailViewModel extends ViewModel {

    private final CardRepository cardRepository;
    private final RewardRateRepository rateRepository;
    private final CustomCategoryRepository customCategoryRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<CustomCategory> category = new MutableLiveData<>();
    private final MutableLiveData<List<CustomCategoryDetailFragment.CardRateAdapter.CardRateRow>> cardRows
            = new MutableLiveData<>();

    @Inject
    public CustomCategoryDetailViewModel(CardRepository cardRepository,
                                          RewardRateRepository rateRepository,
                                          CustomCategoryRepository customCategoryRepository) {
        this.cardRepository = cardRepository;
        this.rateRepository = rateRepository;
        this.customCategoryRepository = customCategoryRepository;
    }

    public LiveData<CustomCategory> getCategory() { return category; }
    public LiveData<List<CustomCategoryDetailFragment.CardRateAdapter.CardRateRow>> getCardRows() {
        return cardRows;
    }

    public void load(long catId) {
        executor.execute(() -> {
            // Load existing category if editing
            List<CustomCategory> allCats = customCategoryRepository.getAllCustomCategoriesSync();
            CustomCategory cat = null;
            for (CustomCategory c : allCats) {
                if (c.id == catId) { cat = c; break; }
            }
            if (cat == null) cat = new CustomCategory("", 0);
            category.postValue(cat);

            // Load all cards + current overrides for this category
            List<CardDefinition> allCards = cardRepository.getAllCardDefinitionsSync();
            List<CustomCategoryRate> overrides = customCategoryRepository.getRatesForCategorySync(catId);
            Map<String, CustomCategoryRate> overrideMap = new HashMap<>();
            for (CustomCategoryRate cr : overrides) {
                overrideMap.put(cr.cardDefinitionId, cr);
            }

            // Load GENERAL rates for fallback labels
            List<RewardRate> allRates = rateRepository.getAllRatesSync();
            Map<String, RewardRate> generalRateMap = new HashMap<>();
            for (RewardRate rate : allRates) {
                if (rate.category == RewardCategory.GENERAL && !rate.isCustomized) {
                    RewardRate existing = generalRateMap.get(rate.cardDefinitionId);
                    if (existing == null || rate.rate > existing.rate) {
                        generalRateMap.put(rate.cardDefinitionId, rate);
                    }
                }
            }

            // Build rows — sort cards with overrides first
            List<CustomCategoryDetailFragment.CardRateAdapter.CardRateRow> rows = new ArrayList<>();
            for (CardDefinition card : allCards) {
                RewardRate general = generalRateMap.get(card.id);
                String fallbackLabel;
                if (general != null) {
                    String rateStr = general.rate == Math.floor(general.rate)
                            ? String.valueOf((int) general.rate)
                            : String.valueOf(general.rate);
                    String currency = (general.currencyName != null && !general.currencyName.isEmpty())
                            ? general.currencyName : card.rewardCurrencyName;
                    fallbackLabel = "General: " + rateStr + "x " + currency;
                } else {
                    fallbackLabel = "No general rate";
                }
                rows.add(new CustomCategoryDetailFragment.CardRateAdapter.CardRateRow(
                        card, fallbackLabel, overrideMap.get(card.id)));
            }

            // Sort: cards with overrides first
            rows.sort((a, b) -> {
                boolean aHas = a.existingOverride != null;
                boolean bHas = b.existingOverride != null;
                if (aHas == bHas) return a.card.displayName.compareTo(b.card.displayName);
                return aHas ? -1 : 1;
            });

            cardRows.postValue(rows);
        });
    }

    public void save(long catId, String name,
                     List<CustomCategoryDetailFragment.CardRateAdapter.CardRateRow> dirtyRates,
                     List<String> clearedCardIds, Runnable onComplete) {
        executor.execute(() -> {
            // Update category name
            List<CustomCategory> allCats = customCategoryRepository.getAllCustomCategoriesSync();
            for (CustomCategory c : allCats) {
                if (c.id == catId) {
                    c.name = name;
                    customCategoryRepository.updateCategorySync(c);
                    break;
                }
            }

            // Delete cleared overrides
            for (String cardId : clearedCardIds) {
                customCategoryRepository.deleteRateForCardSync(catId, cardId);
            }

            // Upsert dirty rates
            for (CustomCategoryDetailFragment.CardRateAdapter.CardRateRow row : dirtyRates) {
                double rate;
                try {
                    rate = Double.parseDouble(row.pendingRateText);
                } catch (NumberFormatException e) {
                    continue;
                }
                if (rate <= 0) continue;
                RateType rateType = row.existingOverride != null
                        ? row.existingOverride.rateType : RateType.CASHBACK;
                CustomCategoryRate cr = new CustomCategoryRate(catId, row.card.id, rate, rateType);
                if (row.existingOverride != null) cr.id = row.existingOverride.id;
                customCategoryRepository.upsertRateSync(cr);
            }

            if (onComplete != null) onComplete.run();
        });
    }

    public void delete(long catId, Runnable onComplete) {
        executor.execute(() -> {
            customCategoryRepository.deleteCategorySync(catId);
            if (onComplete != null) onComplete.run();
        });
    }

}
