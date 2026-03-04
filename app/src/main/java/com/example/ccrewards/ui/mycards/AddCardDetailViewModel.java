package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.WelcomeBonusRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddCardDetailViewModel extends ViewModel {

    private final CardRepository cardRepository;
    private final WelcomeBonusRepository wbRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private String cardDefinitionId;

    private final MutableLiveData<CardDefinition> cardDef = new MutableLiveData<>();

    private final MediatorLiveData<List<RewardRate>> rates = new MediatorLiveData<>();
    private LiveData<List<RewardRate>> ratesSource;

    private final MediatorLiveData<List<CardBenefit>> benefits = new MediatorLiveData<>();
    private LiveData<List<CardBenefit>> benefitsSource;

    private WelcomeBonus pendingWelcomeBonus;

    @Inject
    public AddCardDetailViewModel(CardRepository cardRepository,
                                  WelcomeBonusRepository wbRepository) {
        this.cardRepository = cardRepository;
        this.wbRepository = wbRepository;
    }

    public void loadCard(String id) {
        this.cardDefinitionId = id;

        executor.execute(() -> cardDef.postValue(cardRepository.getCardDefinitionSync(id)));

        if (ratesSource != null) rates.removeSource(ratesSource);
        ratesSource = cardRepository.getRatesForCard(id);
        rates.addSource(ratesSource, rates::setValue);

        if (benefitsSource != null) benefits.removeSource(benefitsSource);
        benefitsSource = cardRepository.getBenefitsForCard(id);
        benefits.addSource(benefitsSource, benefits::setValue);
    }

    public LiveData<CardDefinition> getCardDef() { return cardDef; }
    public LiveData<List<RewardRate>> getRates() { return rates; }
    public LiveData<List<CardBenefit>> getBenefits() { return benefits; }

    public void setPendingWelcomeBonus(WelcomeBonus wb) { pendingWelcomeBonus = wb; }
    public void clearPendingWelcomeBonus() { pendingWelcomeBonus = null; }
    public WelcomeBonus getPendingWelcomeBonus() { return pendingWelcomeBonus; }

    public void addUserCard(String nickname, int creditLimitCents, LocalDate openDate,
                            Runnable onComplete) {
        if (cardDefinitionId == null) return;
        UserCard card = new UserCard(
                cardDefinitionId,
                (nickname != null && !nickname.isEmpty()) ? nickname : null,
                creditLimitCents,
                openDate,
                null,
                0);
        cardRepository.addUserCard(card, newId -> {
            if (pendingWelcomeBonus != null) {
                pendingWelcomeBonus.userCardId = newId;
                wbRepository.upsert(pendingWelcomeBonus);
            }
            if (onComplete != null) onComplete.run();
        });
    }
}
