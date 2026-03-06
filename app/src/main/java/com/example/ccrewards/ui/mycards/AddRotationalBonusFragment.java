package com.example.ccrewards.ui.mycards;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RotationalBonus;
import com.example.ccrewards.data.model.RotationalBonusCategory;
import com.example.ccrewards.databinding.FragmentAddRotationalBonusBinding;

import java.util.Locale;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddRotationalBonusFragment extends Fragment {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM d, yyyy");

    /** Maps each standard-category chip ID → the RewardCategory enum it represents. */
    private static final int[] CHIP_IDS = {
            com.example.ccrewards.R.id.chip_cat_dining,
            com.example.ccrewards.R.id.chip_cat_groceries,
            com.example.ccrewards.R.id.chip_cat_travel,
            com.example.ccrewards.R.id.chip_cat_travel_portal,
            com.example.ccrewards.R.id.chip_cat_gas,
            com.example.ccrewards.R.id.chip_cat_entertainment,
            com.example.ccrewards.R.id.chip_cat_online_shopping,
            com.example.ccrewards.R.id.chip_cat_rent,
    };

    private static final RewardCategory[] CHIP_CATEGORIES = {
            RewardCategory.DINING,
            RewardCategory.GROCERIES,
            RewardCategory.TRAVEL,
            RewardCategory.TRAVEL_PORTAL,
            RewardCategory.GAS,
            RewardCategory.ENTERTAINMENT,
            RewardCategory.ONLINE_SHOPPING,
            RewardCategory.RENT_MORTGAGE,
    };

    private FragmentAddRotationalBonusBinding binding;
    private AddRotationalBonusViewModel viewModel;
    private long userCardId;
    private String currencyName;
    private LocalDate selectedEndDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddRotationalBonusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddRotationalBonusViewModel.class);

        userCardId = getArguments() != null ? getArguments().getLong("userCardId", -1) : -1;
        currencyName = getArguments() != null ? getArguments().getString("currencyName", "") : "";

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        // Show the card's earning currency so the user knows the unit
        if (currencyName != null && !currencyName.isEmpty()) {
            binding.tvRbCurrencyLabel.setText("Earning in: " + currencyName);
            binding.tvRbCurrencyLabel.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.tvRbCurrencyLabel.setVisibility(android.view.View.GONE);
        }

        // Defaults
        binding.etRbLabel.setText(defaultLabel());
        selectedEndDate = defaultEndDate();
        binding.etRbEndDate.setText(selectedEndDate.format(DATE_FMT));

        // Date picker
        binding.etRbEndDate.setOnClickListener(v -> showDatePicker());

        binding.btnSaveRotationalBonus.setOnClickListener(v -> save());
    }

    private void showDatePicker() {
        LocalDate initial = selectedEndDate != null ? selectedEndDate : defaultEndDate();
        Calendar cal = Calendar.getInstance();
        cal.set(initial.getYear(), initial.getMonthValue() - 1, initial.getDayOfMonth());

        new DatePickerDialog(requireContext(), (picker, year, month, day) -> {
            selectedEndDate = LocalDate.of(year, month + 1, day);
            binding.etRbEndDate.setText(selectedEndDate.format(DATE_FMT));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void save() {
        // ── Rate ──────────────────────────────────────────────────────────────
        String rateStr = binding.etRbRate.getText() != null
                ? binding.etRbRate.getText().toString().trim() : "";
        if (rateStr.isEmpty()) {
            Toast.makeText(requireContext(), "Enter a bonus rate.", Toast.LENGTH_SHORT).show();
            return;
        }
        double rate;
        try { rate = Double.parseDouble(rateStr); } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Enter a valid number for the rate.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RateType rateType = rateTypeFor(currencyName);

        // ── Collect categories ────────────────────────────────────────────────
        List<RotationalBonusCategory> cats = new ArrayList<>();

        // Standard category chips
        for (int i = 0; i < CHIP_IDS.length; i++) {
            com.google.android.material.chip.Chip chip = binding.getRoot().findViewById(CHIP_IDS[i]);
            if (chip != null && chip.isChecked()) {
                RotationalBonusCategory cat = new RotationalBonusCategory();
                cat.categoryName = CHIP_CATEGORIES[i].name();
                cat.rate = rate;
                cat.rateType = rateType;
                cats.add(cat);
            }
        }

        // Custom / other categories (comma-separated free text)
        String customText = binding.etRbCustomCats.getText() != null
                ? binding.etRbCustomCats.getText().toString() : "";
        for (String part : customText.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                RotationalBonusCategory cat = new RotationalBonusCategory();
                cat.categoryName = trimmed;
                cat.rate = rate;
                cat.rateType = rateType;
                cats.add(cat);
            }
        }

        if (cats.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Select at least one category or enter a custom one.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // ── Build bonus ───────────────────────────────────────────────────────
        String label = binding.etRbLabel.getText() != null
                ? binding.etRbLabel.getText().toString().trim() : "";
        if (label.isEmpty()) label = defaultLabel();

        int limitDollars = 1500;
        String limitStr = binding.etRbLimit.getText() != null
                ? binding.etRbLimit.getText().toString().trim() : "";
        if (!limitStr.isEmpty()) {
            try { limitDollars = Integer.parseInt(limitStr); } catch (NumberFormatException ignored) {}
        }

        RotationalBonus bonus = new RotationalBonus();
        bonus.userCardId = userCardId;
        bonus.label = label;
        bonus.spendLimitCents = limitDollars * 100;
        bonus.usedCents = 0;
        bonus.endDate = selectedEndDate;
        bonus.isFullyUsed = false;

        // Navigate back first, then persist
        Navigation.findNavController(requireView()).navigateUp();
        viewModel.save(bonus, cats, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /** Infers RateType from the card's reward currency name. */
    private static RateType rateTypeFor(String currencyName) {
        if (currencyName == null) return RateType.CASHBACK;
        String lower = currencyName.toLowerCase(Locale.US);
        if (lower.contains("cash")) return RateType.CASHBACK;
        if (lower.contains("mile") || lower.contains("rapid reward") || lower.contains("avios")
                || lower.contains("skyway") || lower.contains("skymile")) return RateType.MILES;
        return RateType.POINTS;
    }

    private static LocalDate defaultEndDate() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int m = today.getMonthValue();
        if (m <= 3)  return LocalDate.of(year, 3, 31);
        if (m <= 6)  return LocalDate.of(year, 6, 30);
        if (m <= 9)  return LocalDate.of(year, 9, 30);
        return LocalDate.of(year, 12, 31);
    }

    private static String defaultLabel() {
        LocalDate today = LocalDate.now();
        int quarter = (today.getMonthValue() - 1) / 3 + 1;
        return "Q" + quarter + " " + today.getYear() + " Bonus";
    }
}
