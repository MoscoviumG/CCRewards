package com.example.ccrewards.ui.mycards;

import androidx.lifecycle.ViewModel;

import com.example.ccrewards.data.model.RotationalBonus;
import com.example.ccrewards.data.model.RotationalBonusCategory;
import com.example.ccrewards.data.repository.RotationalBonusRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddRotationalBonusViewModel extends ViewModel {

    private final RotationalBonusRepository repository;

    @Inject
    public AddRotationalBonusViewModel(RotationalBonusRepository repository) {
        this.repository = repository;
    }

    public void save(RotationalBonus bonus, List<RotationalBonusCategory> cats,
                     Runnable onComplete) {
        repository.insert(bonus, cats, onComplete);
    }
}
