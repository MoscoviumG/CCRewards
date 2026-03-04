package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.databinding.BottomSheetWelcomeBonusBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** BottomSheet for adding or editing a welcome bonus. No @AndroidEntryPoint — no DI needed. */
public class WelcomeBonusBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "WelcomeBonusBottomSheet";
    public static final String RESULT_KEY = "wb_result";

    private static final String ARG_CURRENCY  = "currency";
    private static final String ARG_BONUS     = "bonus_pts";
    private static final String ARG_SPEND     = "spend_req";
    private static final String ARG_DEADLINE  = "deadline_epoch"; // -1L = none
    private static final String ARG_SHOW_BC   = "show_bc";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);

    private BottomSheetWelcomeBonusBinding binding;
    private String currencyArg;
    private LocalDate selectedDeadline;

    /** Use for adding a new welcome bonus. */
    public static WelcomeBonusBottomSheet newInstance(String currencyName) {
        WelcomeBonusBottomSheet sheet = new WelcomeBonusBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENCY, currencyName);
        args.putInt(ARG_BONUS, 0);
        args.putInt(ARG_SPEND, 0);
        args.putLong(ARG_DEADLINE, -1L);
        args.putBoolean(ARG_SHOW_BC, true);
        sheet.setArguments(args);
        return sheet;
    }

    /** Use for editing an existing welcome bonus (pre-populates fields). */
    public static WelcomeBonusBottomSheet newInstance(WelcomeBonus existing) {
        WelcomeBonusBottomSheet sheet = new WelcomeBonusBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENCY, existing.bonusCurrencyName);
        args.putInt(ARG_BONUS, existing.bonusPoints);
        args.putInt(ARG_SPEND, existing.spendRequirementCents);
        args.putLong(ARG_DEADLINE, existing.deadline != null ? existing.deadline.toEpochDay() : -1L);
        args.putBoolean(ARG_SHOW_BC, existing.showInBestCard);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetWelcomeBonusBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) return;

        currencyArg = args.getString(ARG_CURRENCY, "");
        int existingBonus = args.getInt(ARG_BONUS, 0);
        int existingSpend = args.getInt(ARG_SPEND, 0);
        long existingDeadlineEpoch = args.getLong(ARG_DEADLINE, -1L);
        boolean showBestCard = args.getBoolean(ARG_SHOW_BC, true);

        // Configure bonus field based on card type
        if (isCashBack(currencyArg)) {
            binding.tilWbBonus.setHint("Bonus amount ($)");
            binding.tvWbCurrencyLabel.setVisibility(View.GONE);
            // Pre-fill: cents → dollars
            if (existingBonus > 0) {
                binding.etWbBonus.setText(String.format(Locale.US, "%.2f", existingBonus / 100.0));
            }
        } else {
            binding.tilWbBonus.setHint("Bonus amount (e.g. 60,000)");
            binding.tvWbCurrencyLabel.setVisibility(View.VISIBLE);
            binding.tvWbCurrencyLabel.setText(currencyArg);
            // Pre-fill: raw integer
            if (existingBonus > 0) {
                binding.etWbBonus.setText(String.valueOf(existingBonus));
            }
        }

        // Pre-fill spend requirement (cents → dollars)
        if (existingSpend > 0) {
            binding.etWbSpend.setText(String.format(Locale.US, "%.2f", existingSpend / 100.0));
        }

        // Pre-fill deadline
        if (existingDeadlineEpoch >= 0) {
            selectedDeadline = LocalDate.ofEpochDay(existingDeadlineEpoch);
            binding.btnWbDeadline.setText(selectedDeadline.format(DATE_FMT));
        }

        binding.switchWbShowBestCard.setChecked(showBestCard);

        // Deadline picker
        binding.btnWbDeadline.setOnClickListener(v -> showDatePicker());

        // Save
        binding.btnWbSave.setOnClickListener(v -> onSave());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select deadline")
                .setSelection(selectedDeadline != null
                        ? selectedDeadline.toEpochDay() * 86_400_000L
                        : MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        picker.addOnPositiveButtonClickListener(selectionMs -> {
            selectedDeadline = Instant.ofEpochMilli(selectionMs)
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDate();
            binding.btnWbDeadline.setText(selectedDeadline.format(DATE_FMT));
        });

        picker.addOnNegativeButtonClickListener(v -> {
            selectedDeadline = null;
            binding.btnWbDeadline.setText("No deadline");
        });

        picker.show(getChildFragmentManager(), "date_picker");
    }

    private void onSave() {
        String bonusStr = binding.etWbBonus.getText() != null
                ? binding.etWbBonus.getText().toString().trim() : "";
        String spendStr = binding.etWbSpend.getText() != null
                ? binding.etWbSpend.getText().toString().trim() : "";

        if (bonusStr.isEmpty()) {
            binding.tilWbBonus.setError("Required");
            return;
        }
        if (spendStr.isEmpty()) {
            binding.tilWbSpend.setError("Required");
            return;
        }

        double bonusDouble;
        double spendDouble;
        try {
            bonusDouble = Double.parseDouble(bonusStr);
            spendDouble = Double.parseDouble(spendStr);
        } catch (NumberFormatException e) {
            return;
        }

        if (bonusDouble <= 0) {
            binding.tilWbBonus.setError("Must be > 0");
            return;
        }
        if (spendDouble <= 0) {
            binding.tilWbSpend.setError("Must be > 0");
            return;
        }

        // Cash back: store bonus as cents; Points: store raw integer
        int bonusValue = isCashBack(currencyArg)
                ? (int) (bonusDouble * 100)
                : (int) bonusDouble;
        int spendCents = (int) (spendDouble * 100);

        Bundle result = new Bundle();
        result.putInt("bonus_points", bonusValue);
        result.putInt("spend_req_cents", spendCents);
        result.putLong("deadline_epoch",
                selectedDeadline != null ? selectedDeadline.toEpochDay() : -1L);
        result.putBoolean("show_in_best_card", binding.switchWbShowBestCard.isChecked());

        getParentFragmentManager().setFragmentResult(RESULT_KEY, result);
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /** Returns true if the currency name indicates a cash-back card. */
    public static boolean isCashBack(String currencyName) {
        return currencyName != null && currencyName.toLowerCase(Locale.US).contains("cash");
    }
}
