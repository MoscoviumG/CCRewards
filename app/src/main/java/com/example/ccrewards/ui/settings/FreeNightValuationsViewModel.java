package com.example.ccrewards.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.FreeNightValuation;
import com.example.ccrewards.data.repository.FreeNightRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FreeNightValuationsViewModel extends ViewModel {

    private final FreeNightRepository repository;

    @Inject
    public FreeNightValuationsViewModel(FreeNightRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<FreeNightValuation>> getValuations() {
        return repository.getAllValuations();
    }

    public void updateValuation(FreeNightValuation valuation) {
        repository.updateValuation(valuation);
    }

    public void resetToDefault(String typeKey) {
        repository.resetValuationToDefault(typeKey);
    }
}
