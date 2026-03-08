package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.ResetType;
import com.example.ccrewards.data.repository.BenefitRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddEditBenefitViewModel extends ViewModel {

    private final BenefitRepository benefitRepository;
    private final MutableLiveData<CardBenefit> existingBenefit = new MutableLiveData<>();
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Inject
    public AddEditBenefitViewModel(BenefitRepository benefitRepository) {
        this.benefitRepository = benefitRepository;
    }

    public void loadBenefit(long benefitId) {
        if (benefitId == -1L) {
            existingBenefit.setValue(null);
            return;
        }
        executor.execute(() -> {
            CardBenefit b = benefitRepository.getBenefitByIdSync(benefitId);
            existingBenefit.postValue(b);
        });
    }

    public LiveData<CardBenefit> getExistingBenefit() {
        return existingBenefit;
    }

    public void saveBenefit(String cardDefinitionId, String name, String description,
                            int amountCents, ResetPeriod resetPeriod, ResetType resetType,
                            Integer customMonth, Integer customDay,
                            long editingBenefitId, Runnable onComplete) {
        if (editingBenefitId == -1L) {
            CardBenefit benefit = new CardBenefit(
                    cardDefinitionId, name, description, amountCents, resetPeriod, true, resetType);
            benefit.customResetMonth = customMonth;
            benefit.customResetDay = customDay;
            benefitRepository.insertBenefit(benefit, onComplete);
        } else {
            CardBenefit current = existingBenefit.getValue();
            if (current != null) {
                current.name = name;
                current.description = description;
                current.amountCents = amountCents;
                current.resetPeriod = resetPeriod;
                current.resetType = resetType;
                current.customResetMonth = customMonth;
                current.customResetDay = customDay;
                benefitRepository.updateBenefit(current);
                if (onComplete != null) onComplete.run();
            }
        }
    }

    public void deleteBenefit(Runnable onComplete) {
        CardBenefit current = existingBenefit.getValue();
        if (current != null) {
            benefitRepository.deleteBenefit(current);
            if (onComplete != null) onComplete.run();
        }
    }
}
