package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.repository.CardRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCardViewModel extends ViewModel {

    public enum Filter { ALL, PERSONAL, BUSINESS }

    private final CardRepository cardRepository;

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Filter> filterMode = new MutableLiveData<>(Filter.ALL);
    private final MediatorLiveData<List<CardDefinition>> displayedCards = new MediatorLiveData<>();

    private LiveData<List<CardDefinition>> allCards;

    @Inject
    public AddCardViewModel(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        allCards = cardRepository.getAllCardDefinitions();

        displayedCards.addSource(allCards, cards -> refilter(cards, searchQuery.getValue(), filterMode.getValue()));
        displayedCards.addSource(searchQuery, q -> refilter(allCards.getValue(), q, filterMode.getValue()));
        displayedCards.addSource(filterMode, f -> refilter(allCards.getValue(), searchQuery.getValue(), f));
    }

    public LiveData<List<CardDefinition>> getDisplayedCards() {
        return displayedCards;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query == null ? "" : query.toLowerCase().trim());
    }

    public void setFilter(Filter filter) {
        filterMode.setValue(filter);
    }

    private void refilter(List<CardDefinition> cards, String query, Filter filter) {
        if (cards == null) {
            displayedCards.setValue(new ArrayList<>());
            return;
        }
        List<CardDefinition> result = new ArrayList<>();
        String q = query == null ? "" : query;
        for (CardDefinition card : cards) {
            // Filter by personal/business
            if (filter == Filter.PERSONAL && card.isBusinessCard) continue;
            if (filter == Filter.BUSINESS && !card.isBusinessCard) continue;
            // Filter by search query
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
