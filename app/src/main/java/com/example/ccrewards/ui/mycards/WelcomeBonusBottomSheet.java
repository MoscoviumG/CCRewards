package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.databinding.BottomSheetWelcomeBonusBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** BottomSheet for adding or editing a welcome bonus. No @AndroidEntryPoint — no DI needed. */
public class WelcomeBonusBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "WelcomeBonusBottomSheet";
    public static final String RESULT_KEY = "wb_result";

    private static final String ARG_CURRENCY     = "currency";
    private static final String ARG_BONUS        = "bonus_pts";
    private static final String ARG_SPEND        = "spend_req";
    private static final String ARG_DEADLINE     = "deadline_epoch"; // -1L = none
    private static final String ARG_SHOW_BC      = "show_bc";
    private static final String ARG_CASHBACK     = "cashback_cents";
    private static final String ARG_FN_TYPE_KEY  = "fn_type_key";
    private static final String ARG_FN_COUNT     = "fn_count";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);

    /**
     * Hardcoded free night type menu — typeKey → display label.
     * Order matches FreeNightValuationSeedData to keep them in sync.
     */
    private static final String[] FN_TYPE_KEYS = {
        "HILTON_UNLIMITED",
        "MARRIOTT_35000",
        "MARRIOTT_85000",
        "IHG_40000",
        "IHG_60000",
        "IHG_100000",
        "HYATT_CAT_1",
        "HYATT_CAT_2",
        "HYATT_CAT_3",
        "HYATT_CAT_4",
        "HYATT_CAT_5",
        "HYATT_CAT_6",
        "HYATT_CAT_7",
        "WYNDHAM_30000",
    };

    private static final String[] FN_DISPLAY_NAMES = {
        "Hilton – Unlimited",
        "Marriott – 35k",
        "Marriott – 85k",
        "IHG – 40k",
        "IHG – 60k",
        "IHG – 100k",
        "Hyatt – Category 1",
        "Hyatt – Category 2",
        "Hyatt – Category 3",
        "Hyatt – Category 4",
        "Hyatt – Category 5",
        "Hyatt – Category 6",
        "Hyatt – Category 7",
        "Wyndham – 30k",
    };

    private BottomSheetWelcomeBonusBinding binding;
    private String currencyArg;
    private LocalDate selectedDeadline;

    // Free night state
    private String pendingFnTypeKey = null; // null = no free nights
    private int pendingFnCount = 1;

    // ── Factory methods ───────────────────────────────────────────────────────

    /** Use for adding a new welcome bonus. */
    public static WelcomeBonusBottomSheet newInstance(String currencyName) {
        WelcomeBonusBottomSheet sheet = new WelcomeBonusBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENCY, currencyName);
        args.putInt(ARG_BONUS, 0);
        args.putInt(ARG_SPEND, 0);
        args.putLong(ARG_DEADLINE, -1L);
        args.putBoolean(ARG_SHOW_BC, true);
        args.putInt(ARG_CASHBACK, 0);
        args.putString(ARG_FN_TYPE_KEY, null);
        args.putInt(ARG_FN_COUNT, 1);
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
        args.putInt(ARG_CASHBACK, existing.cashbackCents);
        args.putString(ARG_FN_TYPE_KEY, null); // free nights managed separately from card detail
        args.putInt(ARG_FN_COUNT, 1);
        sheet.setArguments(args);
        return sheet;
    }

    /** Use for editing an existing WB with known free night details pre-populated. */
    public static WelcomeBonusBottomSheet newInstance(WelcomeBonus existing,
                                                      @Nullable String fnTypeKey, int fnCount) {
        WelcomeBonusBottomSheet sheet = newInstance(existing);
        Bundle args = sheet.getArguments();
        if (args != null) {
            args.putString(ARG_FN_TYPE_KEY, fnTypeKey);
            args.putInt(ARG_FN_COUNT, fnCount);
        }
        return sheet;
    }

    // ── Fragment lifecycle ────────────────────────────────────────────────────

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
        int existingCashback = args.getInt(ARG_CASHBACK, 0);
        pendingFnTypeKey = args.getString(ARG_FN_TYPE_KEY, null);
        pendingFnCount = args.getInt(ARG_FN_COUNT, 1);

        // Configure bonus field based on card type
        if (isCashBack(currencyArg)) {
            binding.tilWbBonus.setHint("Bonus amount ($)");
            binding.tvWbCurrencyLabel.setVisibility(View.GONE);
            if (existingBonus > 0) {
                binding.etWbBonus.setText(String.format(Locale.US, "%.2f", existingBonus / 100.0));
            }
        } else {
            binding.tilWbBonus.setHint("Bonus amount (e.g. 60,000)");
            binding.tvWbCurrencyLabel.setVisibility(View.VISIBLE);
            binding.tvWbCurrencyLabel.setText(currencyArg);
            if (existingBonus > 0) {
                binding.etWbBonus.setText(String.valueOf(existingBonus));
            }
        }

        // Cashback field — hidden for pure cash-back cards
        binding.tilWbCashback.setVisibility(isCashBack(currencyArg) ? View.GONE : View.VISIBLE);
        if (!isCashBack(currencyArg) && existingCashback > 0) {
            binding.etWbCashback.setText(String.format(Locale.US, "%.2f", existingCashback / 100.0));
        }

        // Spend requirement
        if (existingSpend > 0) {
            binding.etWbSpend.setText(String.format(Locale.US, "%.2f", existingSpend / 100.0));
        }

        // Deadline
        if (existingDeadlineEpoch >= 0) {
            selectedDeadline = LocalDate.ofEpochDay(existingDeadlineEpoch);
            binding.btnWbDeadline.setText(selectedDeadline.format(DATE_FMT));
        }

        binding.switchWbShowBestCard.setChecked(showBestCard);

        // Free night summary
        updateFnSummary();
        binding.btnWbFnEdit.setOnClickListener(v -> showFnPickerDialog());

        binding.btnWbDeadline.setOnClickListener(v -> showDatePicker());
        binding.btnWbSave.setOnClickListener(v -> onSave());
    }

    // ── Free night picker ─────────────────────────────────────────────────────

    private void updateFnSummary() {
        if (pendingFnTypeKey == null) {
            binding.tvWbFnSummary.setText("None");
        } else {
            String label = displayNameForTypeKey(pendingFnTypeKey);
            binding.tvWbFnSummary.setText(pendingFnCount + "× " + label);
        }
    }

    private void showFnPickerDialog() {
        // Build display list: "None" + all known types
        String[] menuItems = new String[FN_DISPLAY_NAMES.length + 1];
        menuItems[0] = "None (no free nights)";
        System.arraycopy(FN_DISPLAY_NAMES, 0, menuItems, 1, FN_DISPLAY_NAMES.length);

        // Find currently selected index
        int checkedItem = 0;
        if (pendingFnTypeKey != null) {
            for (int i = 0; i < FN_TYPE_KEYS.length; i++) {
                if (FN_TYPE_KEYS[i].equals(pendingFnTypeKey)) {
                    checkedItem = i + 1;
                    break;
                }
            }
        }

        float dp = requireContext().getResources().getDisplayMetrics().density;
        int pad = (int) (16 * dp);
        int padSm = (int) (8 * dp);

        // Custom view: Spinner for type + NumberPicker for count
        // setSingleChoiceItems and setView/setMessage conflict — use setView only
        android.widget.Spinner spinner = new android.widget.Spinner(requireContext());
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                menuItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(checkedItem);

        TextView tvCount = new TextView(requireContext());
        tvCount.setText("How many?");
        tvCount.setPadding(0, padSm, 0, 0);

        NumberPicker np = new NumberPicker(requireContext());
        np.setMinValue(1);
        np.setMaxValue(10);
        np.setValue(pendingFnCount);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(pad, padSm, pad, 0);
        layout.addView(spinner);
        layout.addView(tvCount);
        layout.addView(np);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Free Night Certificates")
                .setView(layout)
                .setPositiveButton("OK", (d, w) -> {
                    int idx = spinner.getSelectedItemPosition();
                    if (idx == 0) {
                        pendingFnTypeKey = null;
                        pendingFnCount = 1;
                    } else {
                        pendingFnTypeKey = FN_TYPE_KEYS[idx - 1];
                        pendingFnCount = np.getValue();
                    }
                    updateFnSummary();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static String displayNameForTypeKey(String typeKey) {
        for (int i = 0; i < FN_TYPE_KEYS.length; i++) {
            if (FN_TYPE_KEYS[i].equals(typeKey)) return FN_DISPLAY_NAMES[i];
        }
        return typeKey;
    }

    // ── Deadline picker ───────────────────────────────────────────────────────

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

    // ── Save ──────────────────────────────────────────────────────────────────

    private void onSave() {
        String bonusStr = binding.etWbBonus.getText() != null
                ? binding.etWbBonus.getText().toString().trim() : "";
        String spendStr = binding.etWbSpend.getText() != null
                ? binding.etWbSpend.getText().toString().trim() : "";

        // Bonus points/amount is optional when free nights cover the whole bonus
        boolean hasFreeNight = pendingFnTypeKey != null;
        if (bonusStr.isEmpty() && !hasFreeNight) {
            binding.tilWbBonus.setError("Required");
            return;
        }
        if (spendStr.isEmpty()) {
            binding.tilWbSpend.setError("Required");
            return;
        }

        double bonusDouble = 0;
        double spendDouble;
        try {
            if (!bonusStr.isEmpty()) bonusDouble = Double.parseDouble(bonusStr);
            spendDouble = Double.parseDouble(spendStr);
        } catch (NumberFormatException e) {
            return;
        }

        if (!bonusStr.isEmpty() && bonusDouble <= 0) { binding.tilWbBonus.setError("Must be > 0"); return; }
        if (spendDouble <= 0) { binding.tilWbSpend.setError("Must be > 0"); return; }

        int bonusValue = isCashBack(currencyArg)
                ? (int) (bonusDouble * 100)
                : (int) bonusDouble;
        int spendCents = (int) (spendDouble * 100);

        String cashbackStr = binding.etWbCashback.getText() != null
                ? binding.etWbCashback.getText().toString().trim() : "";
        int cashbackCents = 0;
        if (!cashbackStr.isEmpty()) {
            try { cashbackCents = (int) (Double.parseDouble(cashbackStr) * 100); }
            catch (NumberFormatException ignored) {}
        }

        Bundle result = new Bundle();
        result.putInt("bonus_points", bonusValue);
        result.putInt("spend_req_cents", spendCents);
        result.putLong("deadline_epoch",
                selectedDeadline != null ? selectedDeadline.toEpochDay() : -1L);
        result.putBoolean("show_in_best_card", binding.switchWbShowBestCard.isChecked());
        result.putInt("cashback_cents", cashbackCents);
        result.putString("fn_type_key", pendingFnTypeKey != null ? pendingFnTypeKey : "");
        result.putInt("fn_count", pendingFnCount);

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
