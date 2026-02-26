package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.data.repository.CardRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MyCardsViewModel extends ViewModel {

    public enum Filter { ALL, PERSONAL, BUSINESS }

    private Filter currentFilter = Filter.ALL;
    private final MediatorLiveData<List<UserCardWithDetails>> filteredCards = new MediatorLiveData<>();
    private final LiveData<List<UserCardWithDetails>> sourceCards;
    private final CardRepository cardRepository;

    @Inject
    public MyCardsViewModel(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        sourceCards = cardRepository.getActiveUserCards();
        filteredCards.addSource(sourceCards, this::applyFilter);
    }

    public LiveData<List<UserCardWithDetails>> getFilteredCards() {
        return filteredCards;
    }

    public void setFilter(Filter filter) {
        currentFilter = filter;
        applyFilter(sourceCards.getValue());
    }

    private void applyFilter(List<UserCardWithDetails> cards) {
        if (cards == null) {
            filteredCards.setValue(new ArrayList<>());
            return;
        }
        if (currentFilter == Filter.ALL) {
            filteredCards.setValue(new ArrayList<>(cards));
            return;
        }
        List<UserCardWithDetails> result = new ArrayList<>();
        for (UserCardWithDetails item : cards) {
            if (item.definition == null) continue;
            boolean isBiz = item.definition.isBusinessCard;
            if ((currentFilter == Filter.PERSONAL && !isBiz) ||
                    (currentFilter == Filter.BUSINESS && isBiz)) {
                result.add(item);
            }
        }
        filteredCards.setValue(result);
    }

    public void deleteUserCard(UserCard card) {
        cardRepository.deleteUserCard(card);
    }
}
