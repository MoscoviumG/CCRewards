package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.ui.common.CardFilterState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCardViewModel extends ViewModel {

    private final CardRepository cardRepository;

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<CardFilterState> filterState = new MutableLiveData<>(new CardFilterState());
    private final MediatorLiveData<List<CardDefinition>> displayedCards = new MediatorLiveData<>();

    private LiveData<List<CardDefinition>> allCards;

    @Inject
    public AddCardViewModel(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        allCards = cardRepository.getAllCardDefinitions();

        displayedCards.addSource(allCards, cards -> refilter());
        displayedCards.addSource(searchQuery, q -> refilter());
        displayedCards.addSource(filterState, f -> refilter());
    }

    public LiveData<List<CardDefinition>> getDisplayedCards() {
        return displayedCards;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query == null ? "" : query.toLowerCase().trim());
    }

    public void setFilter(CardFilterState filter) {
        filterState.setValue(filter != null ? filter : new CardFilterState());
    }

    public CardFilterState getCurrentFilter() {
        CardFilterState f = filterState.getValue();
        return f != null ? f : new CardFilterState();
    }

    private void refilter() {
        List<CardDefinition> cards = allCards.getValue();
        if (cards == null) {
            displayedCards.setValue(new ArrayList<>());
            return;
        }
        String q = searchQuery.getValue() != null ? searchQuery.getValue() : "";
        CardFilterState filter = filterState.getValue();
        if (filter == null) filter = new CardFilterState();

        List<CardDefinition> result = new ArrayList<>();
        for (CardDefinition card : cards) {
            // Card type
            if (filter.cardType == CardFilterState.CardType.PERSONAL && card.isBusinessCard) continue;
            if (filter.cardType == CardFilterState.CardType.BUSINESS && !card.isBusinessCard) continue;
            // Issuer
            if (!filter.issuers.isEmpty() && !filter.issuers.contains(card.issuer)) continue;
            // Network
            if (!filter.networks.isEmpty() && !filter.networks.contains(card.network)) continue;
            // Search query
            if (!q.isEmpty()) {
                boolean matches = card.displayName.toLowerCase().contains(q)
                        || card.issuer.toLowerCase().contains(q)
                        || card.rewardCurrencyName.toLowerCase().contains(q);
                if (!matches) continue;
            }
            result.add(card);
        }
        displayedCards.setValue(result);
    }

    public void addUserCard(CardDefinition definition, String nickname, int creditLimit,
                            LocalDate openDate, Runnable onComplete) {
        UserCard card = new UserCard(
                definition.id,
                (nickname != null && !nickname.isEmpty()) ? nickname : null,
                creditLimit,
                openDate,
                null,
                0);
        cardRepository.addUserCard(card, onComplete);
    }

    public void createCustomCard(String name, String issuer, int annualFee,
                                 String nickname, int creditLimit, LocalDate openDate,
                                 Runnable onComplete) {
        String cardId = "custom_" + System.currentTimeMillis();
        long color = 0xFF607D8B; // Blue-grey default
        CardDefinition def = new CardDefinition(
                cardId, name, issuer, "Visa", annualFee,
                true, false, color, 0xFF90A4AE, "Cash Back");
        cardRepository.insertCardDefinition(def);

        UserCard card = new UserCard(
                cardId,
                (nickname != null && !nickname.isEmpty()) ? nickname : null,
                creditLimit,
                openDate,
                null,
                0);
        cardRepository.addUserCard(card, onComplete);
    }
}
