package com.example.ccrewards.ui.mycards;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.ResetType;
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
    private Integer customMonth = null;
    private Integer customDay = null;

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

        if (isEditing) {
            binding.toolbar.inflateMenu(R.menu.menu_add_edit_benefit);
            binding.toolbar.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_delete_benefit) {
                    confirmDeleteBenefit(view);
                    return true;
                }
                return false;
            });
        }

        viewModel.loadBenefit(benefitId);

        // Period selection drives show/hide of reset type section and date picker
        binding.rgResetPeriod.setOnCheckedChangeListener((group, checkedId) ->
                updateResetSectionVisibility());

        // Reset type selection drives show/hide of custom date row
        binding.rgResetType.setOnCheckedChangeListener((group, checkedId) ->
                updateResetSectionVisibility());

        binding.btnPickCustomDate.setOnClickListener(v -> {
            java.time.LocalDate today = java.time.LocalDate.now();
            boolean isOneTime = binding.rgResetPeriod.getCheckedRadioButtonId() == binding.rbOneTime.getId();
            int initYear  = isOneTime ? today.getYear() : 2000;
            int initMonth = customMonth != null ? customMonth - 1 : today.getMonthValue() - 1;
            int initDay   = customDay   != null ? customDay       : today.getDayOfMonth();
            DatePickerDialog dpd = new DatePickerDialog(requireContext(),
                    (picker, year, month0, dayOfMonth) -> {
                        customMonth = month0 + 1;
                        customDay   = dayOfMonth;
                        binding.tvCustomDateLabel.setText(
                                java.time.Month.of(customMonth).getDisplayName(
                                        java.time.format.TextStyle.SHORT,
                                        java.util.Locale.getDefault()) + " " + customDay);
                    }, initYear, initMonth, initDay);
            // Hide year spinner — we only want month+day
            try {
                java.lang.reflect.Field f = dpd.getDatePicker().getClass().getDeclaredField("mYearSpinner");
                f.setAccessible(true);
                android.view.View yearSpinner = (android.view.View) f.get(dpd.getDatePicker());
                if (yearSpinner != null) yearSpinner.setVisibility(View.GONE);
            } catch (Exception ignored) {}
            dpd.show();
        });

        // Populate fields if editing
        viewModel.getExistingBenefit().observe(getViewLifecycleOwner(), benefit -> {
            if (benefit != null) {
                binding.etBenefitName.setText(benefit.name);
                binding.etBenefitDescription.setText(benefit.description);
                binding.etBenefitAmount.setText(String.valueOf(benefit.amountCents / 100));

                if (benefit.isOneTime) {
                    binding.rgResetPeriod.check(binding.rbOneTime.getId());
                    // Restore due date if set
                    if (benefit.customResetMonth != null && benefit.customResetDay != null) {
                        customMonth = benefit.customResetMonth;
                        customDay   = benefit.customResetDay;
                        binding.tvCustomDateLabel.setText(
                                java.time.Month.of(customMonth).getDisplayName(
                                        java.time.format.TextStyle.SHORT,
                                        java.util.Locale.getDefault()) + " " + customDay);
                    }
                } else {
                    // Set reset period radio
                    switch (benefit.resetPeriod) {
                        case MONTHLY:       binding.rgResetPeriod.check(binding.rbMonthly.getId()); break;
                        case QUARTERLY:     binding.rgResetPeriod.check(binding.rbQuarterly.getId()); break;
                        case SEMI_ANNUALLY: binding.rgResetPeriod.check(binding.rbSemiAnnually.getId()); break;
                        default:            binding.rgResetPeriod.check(binding.rbAnnually.getId()); break;
                    }
                    // Set reset type radio
                    switch (benefit.resetType) {
                        case ANNIVERSARY:
                            binding.rgResetType.check(binding.rbAnniversary.getId());
                            break;
                        case CUSTOM:
                            binding.rgResetType.check(binding.rbCustom.getId());
                            if (benefit.customResetMonth != null && benefit.customResetDay != null) {
                                customMonth = benefit.customResetMonth;
                                customDay   = benefit.customResetDay;
                                binding.tvCustomDateLabel.setText(
                                        java.time.Month.of(customMonth).getDisplayName(
                                                java.time.format.TextStyle.SHORT,
                                                java.util.Locale.getDefault()) + " " + customDay);
                            }
                            break;
                        default:
                            binding.rgResetType.check(binding.rbCalendar.getId());
                            break;
                    }
                }
                updateResetSectionVisibility();
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

            boolean isOneTime = binding.rgResetPeriod.getCheckedRadioButtonId() == binding.rbOneTime.getId();

            if (isOneTime) {
                // For one-time benefits, store ANNUALLY as the period fallback.
                // The due date (if set) is stored in customResetMonth/Day; resetType = CUSTOM when set.
                boolean hasDueDate = customMonth != null && customDay != null;
                ResetType resetType = hasDueDate ? ResetType.CUSTOM : ResetType.CALENDAR;
                viewModel.saveBenefit(cardDefinitionId, name, description, amountCents,
                        ResetPeriod.ANNUALLY, resetType,
                        hasDueDate ? customMonth : null, hasDueDate ? customDay : null,
                        true, benefitId,
                        () -> requireActivity().runOnUiThread(() ->
                                Navigation.findNavController(requireView()).navigateUp()));
            } else {
                ResetPeriod period = getSelectedPeriod();
                ResetType resetType = getSelectedResetType();

                if (resetType == ResetType.CUSTOM && (customMonth == null || customDay == null)) {
                    Snackbar.make(v, "Please pick a start date for Custom reset type", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Integer saveMonth = resetType == ResetType.CUSTOM ? customMonth : null;
                Integer saveDay   = resetType == ResetType.CUSTOM ? customDay   : null;

                viewModel.saveBenefit(cardDefinitionId, name, description, amountCents, period,
                        resetType, saveMonth, saveDay, false, benefitId,
                        () -> requireActivity().runOnUiThread(() ->
                                Navigation.findNavController(requireView()).navigateUp()));
            }
        });
    }

    /** Syncs visibility of the reset type section and date picker based on current selections. */
    private void updateResetSectionVisibility() {
        boolean isOneTime = binding.rgResetPeriod.getCheckedRadioButtonId() == binding.rbOneTime.getId();

        // Show/hide reset type radio group and its label
        int resetTypeVisibility = isOneTime ? View.GONE : View.VISIBLE;
        binding.tvResetTypeLabel.setVisibility(resetTypeVisibility);
        binding.rgResetType.setVisibility(resetTypeVisibility);

        // Show date picker:
        // - One-time mode: always show it (optional due date), labeled "Due date (optional):"
        // - Recurring mode: only when Custom reset type is selected, labeled "Period starts on:"
        if (isOneTime) {
            binding.layoutCustomDate.setVisibility(View.VISIBLE);
            binding.tvCustomDatePrefix.setText("Due date (optional):");
        } else {
            boolean showCustom = binding.rgResetType.getCheckedRadioButtonId() == binding.rbCustom.getId();
            binding.layoutCustomDate.setVisibility(showCustom ? View.VISIBLE : View.GONE);
            binding.tvCustomDatePrefix.setText("Period starts on:");
        }
    }

    private void confirmDeleteBenefit(View navView) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Benefit")
                .setMessage("Remove this benefit?")
                .setPositiveButton("Delete", (dialog, which) ->
                        viewModel.deleteBenefit(() ->
                                requireActivity().runOnUiThread(() ->
                                        Navigation.findNavController(navView).navigateUp())))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private ResetPeriod getSelectedPeriod() {
        int checkedId = binding.rgResetPeriod.getCheckedRadioButtonId();
        if (checkedId == binding.rbMonthly.getId())      return ResetPeriod.MONTHLY;
        if (checkedId == binding.rbQuarterly.getId())    return ResetPeriod.QUARTERLY;
        if (checkedId == binding.rbSemiAnnually.getId()) return ResetPeriod.SEMI_ANNUALLY;
        return ResetPeriod.ANNUALLY;
    }

    private ResetType getSelectedResetType() {
        int id = binding.rgResetType.getCheckedRadioButtonId();
        if (id == binding.rbAnniversary.getId()) return ResetType.ANNIVERSARY;
        if (id == binding.rbCustom.getId())      return ResetType.CUSTOM;
        return ResetType.CALENDAR;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
