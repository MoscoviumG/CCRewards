package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.databinding.FragmentAddEditBenefitBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddEditBenefitFragment extends Fragment {

    private FragmentAddEditBenefitBinding binding;
    private AddEditBenefitViewModel viewModel;
    private String cardDefinitionId;
    private long benefitId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddEditBenefitBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddEditBenefitViewModel.class);

        cardDefinitionId = getArguments() != null ? getArguments().getString("cardDefinitionId") : null;
        benefitId = getArguments() != null ? getArguments().getLong("benefitId", -1L) : -1L;

        boolean isEditing = benefitId != -1L;
        binding.toolbar.setTitle(isEditing ? "Edit Benefit" : "Add Benefit");
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        viewModel.loadBenefit(benefitId);

        // Populate fields if editing
        viewModel.getExistingBenefit().observe(getViewLifecycleOwner(), benefit -> {
            if (benefit != null) {
                binding.etBenefitName.setText(benefit.name);
                binding.etBenefitDescription.setText(benefit.description);
                // Convert cents to dollars for display
                binding.etBenefitAmount.setText(String.valueOf(benefit.amountCents / 100));
                // Set reset period radio
                switch (benefit.resetPeriod) {
                    case MONTHLY: binding.rgResetPeriod.check(binding.rbMonthly.getId()); break;
                    case QUARTERLY: binding.rgResetPeriod.check(binding.rbQuarterly.getId()); break;
                    case SEMI_ANNUALLY: binding.rgResetPeriod.check(binding.rbSemiAnnually.getId()); break;
                    default: binding.rgResetPeriod.check(binding.rbAnnually.getId()); break;
                }
            }
        });

        // Save FAB
        binding.fabSaveBenefit.setOnClickListener(v -> {
            String name = binding.etBenefitName.getText() != null
                    ? binding.etBenefitName.getText().toString().trim() : "";
            if (name.isEmpty()) {
                Snackbar.make(v, "Please enter a benefit name", Snackbar.LENGTH_SHORT).show();
                return;
            }

            String description = binding.etBenefitDescription.getText() != null
                    ? binding.etBenefitDescription.getText().toString().trim() : "";

            String amtStr = binding.etBenefitAmount.getText() != null
                    ? binding.etBenefitAmount.getText().toString().trim() : "0";
            int amountCents;
            try {
                amountCents = (int) (Double.parseDouble(amtStr.isEmpty() ? "0" : amtStr) * 100);
            } catch (NumberFormatException e) {
                amountCents = 0;
            }

            ResetPeriod period = getSelectedPeriod();

            viewModel.saveBenefit(cardDefinitionId, name, description, amountCents, period,
                    benefitId, () -> requireActivity().runOnUiThread(() ->
                            Navigation.findNavController(requireView()).navigateUp()));
        });
    }

    private ResetPeriod getSelectedPeriod() {
        int checkedId = binding.rgResetPeriod.getCheckedRadioButtonId();
        if (checkedId == binding.rbMonthly.getId()) return ResetPeriod.MONTHLY;
        if (checkedId == binding.rbQuarterly.getId()) return ResetPeriod.QUARTERLY;
        if (checkedId == binding.rbSemiAnnually.getId()) return ResetPeriod.SEMI_ANNUALLY;
        return ResetPeriod.ANNUALLY;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
