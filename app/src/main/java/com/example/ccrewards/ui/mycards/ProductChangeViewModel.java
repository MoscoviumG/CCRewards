package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.ProductChangeRecord;
import com.example.ccrewards.data.repository.CardRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProductChangeViewModel extends ViewModel {

    // Common product change paths: from card ID → list of suggested target IDs
    private static final Map<String, List<String>> COMMON_PC_PATHS = new HashMap<>();
    static {
        COMMON_PC_PATHS.put("chase_sapphire_preferred", Arrays.asList(
                "chase_freedom_unlimited", "chase_freedom_flex", "chase_freedom_rise"));
        COMMON_PC_PATHS.put("chase_sapphire_reserve", Arrays.asList(
                "chase_sapphire_preferred", "chase_freedom_unlimited", "chase_freedom_flex"));
        COMMON_PC_PATHS.put("chase_ink_business_preferred", Arrays.asList(
                "chase_ink_business_cash", "chase_ink_business_unlimited"));
        COMMON_PC_PATHS.put("amex_platinum", Arrays.asList("amex_gold", "amex_green"));
        COMMON_PC_PATHS.put("amex_gold", Arrays.asList("amex_green", "amex_everyday_preferred"));
        COMMON_PC_PATHS.put("citi_strata_premier", Arrays.asList(
                "citi_double_cash", "citi_rewards_plus", "citi_custom_cash"));
        COMMON_PC_PATHS.put("c1_venture_x", Arrays.asList("c1_venture"));
    }

    private final CardRepository cardRepository;
    private final MediatorLiveData<List<CardDefinition>> targets = new MediatorLiveData<>();
    private final MutableLiveData<String> suggestedIds = new MutableLiveData<>();

    private String currentCardId;
    private String issuer;

    @Inject
    public ProductChangeViewModel(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void load(String currentCardDefinitionId, String issuer) {
        this.currentCardId = currentCardDefinitionId;
        this.issuer = issuer;

        // Get all cards from same issuer, excluding current
        LiveData<List<CardDefinition>> issuerCards = cardRepository.getCardsByIssuer(issuer);
        targets.addSource(issuerCards, cards -> {
            if (cards == null) {
                targets.setValue(new ArrayList<>());
                return;
            }
            // Sort: suggested first, then alphabetical
            List<String> suggested = COMMON_PC_PATHS.getOrDefault(currentCardDefinitionId, new ArrayList<>());
            List<CardDefinition> result = new ArrayList<>();
            List<CardDefinition> remaining = new ArrayList<>();
            for (CardDefinition card : cards) {
                if (card.id.equals(currentCardDefinitionId)) continue; // skip current
                if (suggested.contains(card.id)) {
                    result.add(card);
                } else {
                    remaining.add(card);
                }
            }
            result.addAll(remaining);
            targets.setValue(result);
        });
    }

    public LiveData<List<CardDefinition>> getTargets() {
        return targets;
    }

    public boolean isSuggested(String cardId) {
        List<String> suggested = COMMON_PC_PATHS.getOrDefault(currentCardId, new ArrayList<>());
        return suggested.contains(cardId);
    }

    public void performProductChange(long userCardId, String toCardId, Runnable onComplete) {
        ProductChangeRecord record = new ProductChangeRecord(
                userCardId, currentCardId, toCardId, LocalDate.now(), null);
        cardRepository.productChangeCard(userCardId, currentCardId, toCardId, record);
        if (onComplete != null) onComplete.run();
    }
}
