package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.repository.RewardRateRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditRewardRatesViewModel extends ViewModel {

    private final RewardRateRepository rateRepository;
    private final MutableLiveData<List<RewardRate>> rates = new MutableLiveData<>();
    private String cardDefinitionId;

    @Inject
    public EditRewardRatesViewModel(RewardRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public void loadRates(String cardDefinitionId) {
        this.cardDefinitionId = cardDefinitionId;
        // Observe LiveData from repository
        rateRepository.getRatesForCard(cardDefinitionId).observeForever(rates::setValue);
    }

    public LiveData<List<RewardRate>> getRates() {
        return rates;
    }

    public void saveRate(RewardRate rate) {
        rate.isCustomized = true;
        rateRepository.updateRate(rate);
    }

    public void resetAllCustomizations() {
        if (cardDefinitionId != null) {
            rateRepository.resetCustomizations(cardDefinitionId);
        }
    }
}
