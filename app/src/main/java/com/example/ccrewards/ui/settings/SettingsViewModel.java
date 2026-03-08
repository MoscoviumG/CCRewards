package com.example.ccrewards.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.data.repository.CardRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    private static final DateTimeFormatter DROP_FMT = DateTimeFormatter.ofPattern("MMM yyyy", Locale.US);

    /** int[0] = total cards, int[1] = total annual fee, int[2] = 5/24 count */
    private final MediatorLiveData<int[]> stats = new MediatorLiveData<>();
    /** Date when the 5/24 count will drop by 1; null if no cards are in the 24-month window. */
    private final MediatorLiveData<String> nextDropOff = new MediatorLiveData<>();

    @Inject
    public SettingsViewModel(CardRepository cardRepository) {
        LiveData<List<UserCardWithDetails>> allCards = cardRepository.getActiveUserCards();
        stats.addSource(allCards, cards -> {
            if (cards == null) {
                stats.setValue(new int[]{0, 0, 0});
                nextDropOff.setValue(null);
                return;
            }
            int count = cards.size();
            int fee = 0;
            LocalDate cutoff = LocalDate.now().minusMonths(24);
            List<LocalDate> inWindow = new ArrayList<>();
            for (UserCardWithDetails item : cards) {
                if (item.definition != null) fee += item.definition.annualFee;
                LocalDate openDate = item.userCard.openDate;
                if (openDate != null && !openDate.isBefore(cutoff)) {
                    inWindow.add(openDate);
                }
            }
            inWindow.sort(LocalDate::compareTo);
            int fiveTwentyFour = inWindow.size();
            stats.setValue(new int[]{count, fee, fiveTwentyFour});

            // Show when count drops to 4: need (fiveTwentyFour - 4) cards to age out.
            // Those are the oldest ones; the last to age out is at index (fiveTwentyFour - 5).
            if (fiveTwentyFour >= 5) {
                LocalDate keyDate = inWindow.get(fiveTwentyFour - 5);
                nextDropOff.setValue(keyDate.plusMonths(24).format(DROP_FMT));
            } else {
                nextDropOff.setValue(null);
            }
        });
    }

    public LiveData<int[]> getStats() {
        return stats;
    }

    public LiveData<String> getNextDropOff() {
        return nextDropOff;
    }
}
