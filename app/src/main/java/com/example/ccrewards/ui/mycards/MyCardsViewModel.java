package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.ui.common.CardFilterState;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MyCardsViewModel extends ViewModel {

    private CardFilterState currentFilter = new CardFilterState();
    private final MediatorLiveData<List<UserCardWithDetails>> filteredCards = new MediatorLiveData<>();
    private final LiveData<List<UserCardWithDetails>> sourceCards;
    private final CardRepository cardRepository;

    private final MutableLiveData<Integer> totalAnnualFee = new MutableLiveData<>(0);

    @Inject
    public MyCardsViewModel(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        sourceCards = cardRepository.getActiveUserCards();
        filteredCards.addSource(sourceCards, this::applyFilter);
    }

    public LiveData<List<UserCardWithDetails>> getFilteredCards() {
        return filteredCards;
    }

    public LiveData<Integer> getTotalAnnualFee() {
        return totalAnnualFee;
    }

    public void setFilter(CardFilterState filter) {
        currentFilter = filter != null ? filter : new CardFilterState();
        applyFilter(sourceCards.getValue());
    }

    public CardFilterState getCurrentFilter() {
        return currentFilter;
    }

    private void applyFilter(List<UserCardWithDetails> cards) {
        if (cards == null) {
            filteredCards.setValue(new ArrayList<>());
            totalAnnualFee.setValue(0);
            return;
        }

        List<UserCardWithDetails> result;
        if (currentFilter.isDefault()) {
            result = new ArrayList<>(cards);
        } else {
            LocalDate today = LocalDate.now();
            LocalDate nextMonth = today.plusMonths(1);

            result = new ArrayList<>();
            for (UserCardWithDetails item : cards) {
                if (item.definition == null) continue;

                // Card type
                boolean isBiz = item.definition.isBusinessCard;
                if (currentFilter.cardType == CardFilterState.CardType.PERSONAL && isBiz) continue;
                if (currentFilter.cardType == CardFilterState.CardType.BUSINESS && !isBiz) continue;

                // Issuer
                if (!currentFilter.issuers.isEmpty()) {
                    if (!currentFilter.issuers.contains(item.definition.issuer)) continue;
                }

                // Network
                if (!currentFilter.networks.isEmpty()) {
                    if (!currentFilter.networks.contains(item.definition.network)) continue;
                }

                // Anniversary month
                if (currentFilter.anniversaryMonth != CardFilterState.AnniversaryFilter.ANY) {
                    LocalDate openDate = item.userCard.openDate;
                    if (openDate == null) continue;
                    int annivMonth = openDate.getMonthValue();
                    if (currentFilter.anniversaryMonth == CardFilterState.AnniversaryFilter.THIS_MONTH) {
                        if (annivMonth != today.getMonthValue()) continue;
                    } else if (currentFilter.anniversaryMonth == CardFilterState.AnniversaryFilter.NEXT_MONTH) {
                        if (annivMonth != nextMonth.getMonthValue()) continue;
                    }
                }

                // Card age
                if (currentFilter.cardAge != CardFilterState.CardAgeFilter.ANY) {
                    LocalDate openDate = item.userCard.openDate;
                    if (openDate == null) continue;
                    long yearsOld = openDate.until(today, ChronoUnit.YEARS);
                    switch (currentFilter.cardAge) {
                        case LESS_THAN_1:
                            if (yearsOld >= 1) continue;
                            break;
                        case ONE_TO_THREE:
                            if (yearsOld < 1 || yearsOld >= 3) continue;
                            break;
                        case MORE_THAN_THREE:
                            if (yearsOld < 3) continue;
                            break;
                    }
                }

                result.add(item);
            }
        }

        filteredCards.setValue(result);

        // Compute total annual fee across filtered cards
        int fee = 0;
        for (UserCardWithDetails item : result) {
            if (item.definition != null) fee += item.definition.annualFee;
        }
        totalAnnualFee.setValue(fee);
    }

    public void deleteUserCard(UserCard card) {
        cardRepository.deleteUserCard(card);
    }
}
