package com.example.ccrewards.ui.mycards;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
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
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class MyCardsViewModel extends ViewModel {

    public enum SortOrder {
        DEFAULT, OPEN_DATE, ALPHABETICAL, ANNUAL_FEE, ISSUER, ANNIVERSARY_MONTH
    }

    private static final String PREFS_NAME = "mycards_prefs";
    private static final String KEY_SORT_ORDER = "sort_order";

    private CardFilterState currentFilter = new CardFilterState();
    private SortOrder currentSortOrder = SortOrder.DEFAULT;
    private final MediatorLiveData<List<UserCardWithDetails>> filteredCards = new MediatorLiveData<>();
    private final LiveData<List<UserCardWithDetails>> sourceCards;
    private final CardRepository cardRepository;
    private final SharedPreferences prefs;

    @Inject
    public MyCardsViewModel(@ApplicationContext Context context, CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String saved = prefs.getString(KEY_SORT_ORDER, SortOrder.DEFAULT.name());
        try {
            currentSortOrder = SortOrder.valueOf(saved);
        } catch (IllegalArgumentException e) {
            currentSortOrder = SortOrder.DEFAULT;
        }
        sourceCards = cardRepository.getAllUserCards();
        filteredCards.addSource(sourceCards, this::applyFilter);
    }

    public LiveData<List<UserCardWithDetails>> getFilteredCards() {
        return filteredCards;
    }

    public void setFilter(CardFilterState filter) {
        currentFilter = filter != null ? filter : new CardFilterState();
        applyFilter(sourceCards.getValue());
    }

    public CardFilterState getCurrentFilter() {
        return currentFilter;
    }

    public SortOrder getCurrentSortOrder() {
        return currentSortOrder;
    }

    public void setSort(SortOrder sortOrder) {
        currentSortOrder = sortOrder != null ? sortOrder : SortOrder.DEFAULT;
        prefs.edit().putString(KEY_SORT_ORDER, currentSortOrder.name()).apply();
        applyFilter(sourceCards.getValue());
    }

    private void applyFilter(List<UserCardWithDetails> cards) {
        if (cards == null) {
            filteredCards.setValue(new ArrayList<>());
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate nextMonth = today.plusMonths(1);

        List<UserCardWithDetails> result = new ArrayList<>();
        for (UserCardWithDetails item : cards) {
            if (item.definition == null) continue;

            // Closed filter (always applied — default is OPEN_ONLY)
            boolean isClosed = item.userCard.closeDate != null;
            if (currentFilter.closedFilter == CardFilterState.ClosedFilter.OPEN_ONLY && isClosed) continue;
            if (currentFilter.closedFilter == CardFilterState.ClosedFilter.CLOSED_ONLY && !isClosed) continue;

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

        // Apply sort
        switch (currentSortOrder) {
            case OPEN_DATE:
                result.sort((a, b) -> {
                    LocalDate da = a.userCard.openDate, db = b.userCard.openDate;
                    if (da == null && db == null) return 0;
                    if (da == null) return 1;
                    if (db == null) return -1;
                    return da.compareTo(db);
                });
                break;
            case ALPHABETICAL:
                result.sort((a, b) -> {
                    String na = a.definition != null ? a.definition.displayName : "";
                    String nb = b.definition != null ? b.definition.displayName : "";
                    return na.compareToIgnoreCase(nb);
                });
                break;
            case ANNUAL_FEE:
                result.sort((a, b) -> {
                    int fa = a.definition != null ? a.definition.annualFee : 0;
                    int fb = b.definition != null ? b.definition.annualFee : 0;
                    return Integer.compare(fa, fb);
                });
                break;
            case ISSUER:
                result.sort((a, b) -> {
                    String ia = a.definition != null ? a.definition.issuer : "";
                    String ib = b.definition != null ? b.definition.issuer : "";
                    return ia.compareToIgnoreCase(ib);
                });
                break;
            case ANNIVERSARY_MONTH:
                result.sort((a, b) -> {
                    Integer ma = a.userCard.openDate != null ? a.userCard.openDate.getMonthValue() : null;
                    Integer mb = b.userCard.openDate != null ? b.userCard.openDate.getMonthValue() : null;
                    if (ma == null && mb == null) return 0;
                    if (ma == null) return 1;
                    if (mb == null) return -1;
                    return Integer.compare(ma, mb);
                });
                break;
            default:
                result.sort((a, b) -> Integer.compare(a.userCard.sortOrder, b.userCard.sortOrder));
                break;
        }

        filteredCards.setValue(result);
    }

    public void deleteUserCard(UserCard card) {
        cardRepository.deleteUserCard(card);
    }
}
