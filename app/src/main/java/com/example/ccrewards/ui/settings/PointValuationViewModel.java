package com.example.ccrewards.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.data.model.TransferPartner;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.data.repository.RewardRateRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PointValuationViewModel extends ViewModel {

    private final RewardRateRepository rateRepository;
    private final BenefitRepository benefitRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public PointValuationViewModel(RewardRateRepository rateRepository,
                                   BenefitRepository benefitRepository) {
        this.rateRepository = rateRepository;
        this.benefitRepository = benefitRepository;
    }

    public LiveData<List<PointValuation>> getAllValuations() {
        return rateRepository.getAllValuations();
    }

    public void updateValuation(PointValuation valuation) {
        rateRepository.updateValuation(valuation);
    }

    public void resetValuationToDefault(String currencyName) {
        rateRepository.resetValuationToDefault(currencyName);
    }

    public void addValuation(String name, double centsPerPoint, Runnable onDone) {
        executor.execute(() -> {
            rateRepository.insertValuationSync(new PointValuation(name, centsPerPoint, centsPerPoint));
            if (onDone != null) onDone.run();
        });
    }

    public LiveData<List<TransferPartner>> getAirlinePartners(String currencyName) {
        return benefitRepository.getAirlinesForCurrency(currencyName);
    }

    public LiveData<List<TransferPartner>> getHotelPartners(String currencyName) {
        return benefitRepository.getHotelsForCurrency(currencyName);
    }
}
