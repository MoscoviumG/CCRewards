package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.CustomCategory;
import com.example.ccrewards.data.model.CustomCategoryRate;
import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.databinding.FragmentAddCustomCardBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddCustomCardFragment extends Fragment {

    private FragmentAddCustomCardBinding binding;
    private AddCustomCardViewModel viewModel;
    private final AtomicReference<LocalDate> selectedDate = new AtomicReference<>(LocalDate.now());
    private String selectedCardCurrency = "Cash Back";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddCustomCardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddCustomCardViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        // Reward currency picker
        binding.tvSelectedCurrencyCard.setText(selectedCardCurrency);
        binding.btnPickCurrency.setOnClickListener(v -> showCardCurrencyPicker());

        // Open date
        binding.etCustomOpenDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        binding.etCustomOpenDate.setOnClickListener(v -> showDatePicker());

        // Observe pending rates → re-render rows
        viewModel.pendingRates.observe(getViewLifecycleOwner(), rates -> renderRateRows(rates));

        // Observe pending benefits → re-render rows
        viewModel.pendingBenefits.observe(getViewLifecycleOwner(), benefits -> renderBenefitRows(benefits));

        // Add buttons
        binding.btnAddRate.setOnClickListener(v -> showAddRateStep1());
        binding.btnAddBenefit.setOnClickListener(v -> showAddBenefitDialog());

        // Welcome Bonus
        binding.btnSetWbCustom.setOnClickListener(v -> showWbBottomSheet());
        binding.btnWbEditCustom.setOnClickListener(v -> {
            WelcomeBonus wb = viewModel.getPendingWelcomeBonus();
            if (wb != null) {
                WelcomeBonusBottomSheet.newInstance(wb)
                        .show(getChildFragmentManager(), WelcomeBonusBottomSheet.TAG);
            }
        });
        binding.btnWbClearCustom.setOnClickListener(v -> {
            viewModel.clearPendingWelcomeBonus();
            bindWbSummary(null);
        });

        getChildFragmentManager().setFragmentResultListener(
                WelcomeBonusBottomSheet.RESULT_KEY, getViewLifecycleOwner(), (key, result) -> {
                    WelcomeBonus wb = new WelcomeBonus();
                    wb.bonusPoints = result.getInt("bonus_points");
                    wb.bonusCurrencyName = currentCurrencyName();
                    wb.spendRequirementCents = result.getInt("spend_req_cents");
                    long epoch = result.getLong("deadline_epoch", -1L);
                    wb.deadline = epoch == -1L ? null : LocalDate.ofEpochDay(epoch);
                    wb.showInBestCard = result.getBoolean("show_in_best_card", true);
                    viewModel.setPendingWelcomeBonus(wb);
                    bindWbSummary(wb);
                });

        bindWbSummary(viewModel.getPendingWelcomeBonus());

        // Save FAB
        binding.fabSaveCustom.setOnClickListener(v -> saveCard());
    }

    private void saveCard() {
        String name = text(binding.etCustomName);
        if (name.isEmpty()) {
            binding.tilCustomName.setError("Required");
            return;
        }
        binding.tilCustomName.setError(null);

        String issuer = text(binding.etCustomIssuer);
        String feeStr = text(binding.etCustomFee);
        int fee = feeStr.isEmpty() ? 0 : Integer.parseInt(feeStr);
        String currency = currentCurrencyName();
        String nickname = text(binding.etCustomNickname);
        String limitStr = text(binding.etCustomCreditLimit);
        int creditLimit = limitStr.isEmpty() ? 0 : Integer.parseInt(limitStr);
        LocalDate openDate = selectedDate.get() != null ? selectedDate.get() : LocalDate.now();

        viewModel.saveCard(name, issuer, fee, currency, openDate, nickname, creditLimit, () ->
                requireActivity().runOnUiThread(() ->
                        Navigation.findNavController(requireView())
                                .popBackStack(R.id.myCardsFragment, false)));
    }

    private void showCardCurrencyPicker() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<PointValuation> pvs = viewModel.getAllValuationsSync();
            requireActivity().runOnUiThread(() -> {
                List<String> names = new ArrayList<>();
                for (PointValuation pv : pvs) names.add(pv.rewardCurrencyName);
                final String CREATE_NEW = "Create new currency...";
                names.add(CREATE_NEW);
                int currentIdx = Math.max(0, names.indexOf(selectedCardCurrency));
                final int[] sel = {currentIdx};
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Select Reward Currency")
                        .setSingleChoiceItems(names.toArray(new String[0]), currentIdx,
                                (d, which) -> sel[0] = which)
                        .setPositiveButton("Select", (d, w) -> {
                            String chosen = names.get(sel[0]);
                            if (CREATE_NEW.equals(chosen)) {
                                showCreateCurrencyDialog(name -> {
                                    selectedCardCurrency = name;
                                    binding.tvSelectedCurrencyCard.setText(name);
                                });
                            } else {
                                selectedCardCurrency = chosen;
                                binding.tvSelectedCurrencyCard.setText(chosen);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });
    }

    // ── Rate rows ─────────────────────────────────────────────────────────────

    private void renderRateRows(List<AddCustomCardViewModel.PendingRate> rates) {
        binding.llPendingRates.removeAllViews();
        for (int i = 0; i < rates.size(); i++) {
            final int idx = i;
            AddCustomCardViewModel.PendingRate pr = rates.get(i);
            String typeStr = pr.rewardCategory != null
                    ? rateTypeLabel(pr.rateType)
                    : (pr.currencyName.isEmpty() ? "custom" : pr.currencyName);
            String label = pr.displayName + "  " + formatRate(pr.rate) + "x  (" + typeStr + ")";
            binding.llPendingRates.addView(makePendingRow(label, v -> viewModel.removeRate(idx)));
        }
    }

    private void showAddRateStep1() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CustomCategory> allCats = viewModel.getAllCustomCategoriesSync();
            List<PointValuation> valuations = viewModel.getAllValuationsSync();
            requireActivity().runOnUiThread(() -> {
                List<String> labels = new ArrayList<>();
                List<Object> values = new ArrayList<>();

                Set<String> usedStandard = new HashSet<>();
                Set<Long> usedCustom = new HashSet<>();
                List<AddCustomCardViewModel.PendingRate> current = viewModel.pendingRates.getValue();
                if (current != null) {
                    for (AddCustomCardViewModel.PendingRate pr : current) {
                        if (pr.rewardCategory != null) usedStandard.add(pr.rewardCategory.name());
                        else usedCustom.add(pr.customCategoryId);
                    }
                }

                for (RewardCategory cat : RewardCategory.values()) {
                    if (!usedStandard.contains(cat.name())) {
                        labels.add(EditRewardRatesFragment.formatCategory(cat.name()));
                        values.add(cat);
                    }
                }
                for (CustomCategory cat : allCats) {
                    if (!usedCustom.contains(cat.id)) {
                        labels.add(cat.name + " (Custom)");
                        values.add(cat);
                    }
                }

                if (labels.isEmpty()) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("No more categories")
                            .setMessage("All available categories already have a rate.")
                            .setPositiveButton("OK", null).show();
                    return;
                }

                final int[] sel = {0};
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Select Category")
                        .setSingleChoiceItems(labels.toArray(new String[0]), 0,
                                (d, which) -> sel[0] = which)
                        .setPositiveButton("Next", (d, w) -> showAddRateStep2(values.get(sel[0]), valuations))
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });
    }

    private void showAddRateStep2(Object selectedCategory, List<PointValuation> valuations) {
        int padPx = (int) (16 * getResources().getDisplayMetrics().density);
        String categoryLabel = selectedCategory instanceof RewardCategory
                ? EditRewardRatesFragment.formatCategory(((RewardCategory) selectedCategory).name())
                : ((CustomCategory) selectedCategory).name;

        EditText etRate = new EditText(requireContext());
        etRate.setHint("Rate (e.g. 3)");
        etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (selectedCategory instanceof RewardCategory) {
            // STANDARD: show rate + currency picker Spinner
            List<String> currencyNames = new ArrayList<>();
            for (PointValuation pv : valuations) currencyNames.add(pv.rewardCurrencyName);
            final String CREATE_NEW = "Create new currency...";
            currencyNames.add(CREATE_NEW);
            final int[] currencyIdx = {0};

            android.widget.Spinner spinner = new android.widget.Spinner(requireContext());
            android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(
                    requireContext(), android.R.layout.simple_spinner_item, currencyNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> a, View v, int pos, long id) {
                    currencyIdx[0] = pos;
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> a) {}
            });

            android.widget.LinearLayout container = new android.widget.LinearLayout(requireContext());
            container.setOrientation(android.widget.LinearLayout.VERTICAL);
            container.setPadding(padPx, padPx / 2, padPx, 0);
            container.addView(etRate);
            container.addView(spinner);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Rate for " + categoryLabel)
                    .setView(container)
                    .setPositiveButton("Add", (d, w) -> {
                        String rateStr = etRate.getText() != null
                                ? etRate.getText().toString().trim() : "";
                        if (rateStr.isEmpty()) return;
                        try {
                            double rateVal = Double.parseDouble(rateStr);
                            String selectedCurrency = currencyNames.get(currencyIdx[0]);
                            if (CREATE_NEW.equals(selectedCurrency)) {
                                showCreateCurrencyDialog(name -> {
                                    AddCustomCardViewModel.PendingRate pr = new AddCustomCardViewModel.PendingRate();
                                    pr.rate = rateVal;
                                    pr.rateType = EditRewardRatesFragment.rateTypeFromCurrency(name);
                                    pr.rewardCategory = (RewardCategory) selectedCategory;
                                    pr.displayName = categoryLabel;
                                    viewModel.addRate(pr);
                                });
                            } else {
                                AddCustomCardViewModel.PendingRate pr = new AddCustomCardViewModel.PendingRate();
                                pr.rate = rateVal;
                                pr.rateType = EditRewardRatesFragment.rateTypeFromCurrency(selectedCurrency);
                                pr.rewardCategory = (RewardCategory) selectedCategory;
                                pr.displayName = categoryLabel;
                                viewModel.addRate(pr);
                            }
                        } catch (NumberFormatException ignored) {}
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            // CUSTOM: show rate + currency picker
            CustomCategory cat = (CustomCategory) selectedCategory;
            List<String> currencyNames = new ArrayList<>();
            for (PointValuation pv : valuations) currencyNames.add(pv.rewardCurrencyName);
            final String CREATE_NEW = "Create new currency...";
            currencyNames.add(CREATE_NEW);
            final int[] currencyIdx = {0};

            android.widget.Spinner spinner = new android.widget.Spinner(requireContext());
            android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(
                    requireContext(), android.R.layout.simple_spinner_item, currencyNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> a, View v, int pos, long id) {
                    currencyIdx[0] = pos;
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> a) {}
            });

            android.widget.LinearLayout container = new android.widget.LinearLayout(requireContext());
            container.setOrientation(android.widget.LinearLayout.VERTICAL);
            container.setPadding(padPx, padPx / 2, padPx, 0);
            container.addView(etRate);
            container.addView(spinner);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Rate for " + categoryLabel)
                    .setView(container)
                    .setPositiveButton("Add", (d, w) -> {
                        String rateStr = etRate.getText() != null
                                ? etRate.getText().toString().trim() : "";
                        if (rateStr.isEmpty()) return;
                        try {
                            double rateVal = Double.parseDouble(rateStr);
                            String selectedCurrency = currencyNames.get(currencyIdx[0]);
                            if (CREATE_NEW.equals(selectedCurrency)) {
                                showCreateCurrencyDialog(name -> addCustomPendingRate(cat, categoryLabel, rateVal, name));
                            } else {
                                addCustomPendingRate(cat, categoryLabel, rateVal, selectedCurrency);
                            }
                        } catch (NumberFormatException ignored) {}
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void addCustomPendingRate(CustomCategory cat, String displayName, double rate, String currencyName) {
        AddCustomCardViewModel.PendingRate pr = new AddCustomCardViewModel.PendingRate();
        pr.rate = rate;
        pr.rateType = EditRewardRatesFragment.rateTypeFromCurrency(currencyName);
        pr.currencyName = currencyName;
        pr.customCategoryId = cat.id;
        pr.displayName = displayName;
        viewModel.addRate(pr);
    }

    private void showCreateCurrencyDialog(java.util.function.Consumer<String> onCreated) {
        int padPx = (int) (16 * getResources().getDisplayMetrics().density);

        com.google.android.material.textfield.TextInputLayout tilName =
                new com.google.android.material.textfield.TextInputLayout(requireContext());
        tilName.setHint("Currency name");
        com.google.android.material.textfield.TextInputEditText etName =
                new com.google.android.material.textfield.TextInputEditText(requireContext());
        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        etName.setSingleLine(true);
        tilName.addView(etName);

        com.google.android.material.textfield.TextInputLayout tilCpp =
                new com.google.android.material.textfield.TextInputLayout(requireContext());
        tilCpp.setHint("Value in ¢/pt (e.g. 0.5)");
        com.google.android.material.textfield.TextInputEditText etCpp =
                new com.google.android.material.textfield.TextInputEditText(requireContext());
        etCpp.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etCpp.setSingleLine(true);
        tilCpp.addView(etCpp);

        android.widget.LinearLayout container = new android.widget.LinearLayout(requireContext());
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        container.setPadding(padPx, 0, padPx, 0);
        container.addView(tilName);
        container.addView(tilCpp);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Create Currency")
                .setView(container)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = etName.getText() != null
                            ? etName.getText().toString().trim() : "";
                    if (name.isEmpty()) return;
                    String cppStr = etCpp.getText() != null
                            ? etCpp.getText().toString().trim() : "";
                    double cpp = cppStr.isEmpty() ? 1.0 : Double.parseDouble(cppStr);
                    viewModel.insertValuationSync(
                            new PointValuation(name, cpp, cpp),
                            () -> requireActivity().runOnUiThread(() -> onCreated.accept(name)));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Benefit rows ──────────────────────────────────────────────────────────

    private void renderBenefitRows(List<AddCustomCardViewModel.PendingBenefit> benefits) {
        binding.llPendingBenefits.removeAllViews();
        for (int i = 0; i < benefits.size(); i++) {
            final int idx = i;
            AddCustomCardViewModel.PendingBenefit pb = benefits.get(i);
            String label = pb.name + "  $" + (pb.amountCents / 100)
                    + " / " + periodLabel(pb.resetPeriod);
            binding.llPendingBenefits.addView(makePendingRow(label, v -> viewModel.removeBenefit(idx)));
        }
    }

    private void showAddBenefitDialog() {
        int padPx = (int) (16 * getResources().getDisplayMetrics().density);

        EditText etName = new EditText(requireContext());
        etName.setHint("Benefit name *");
        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        EditText etAmount = new EditText(requireContext());
        etAmount.setHint("Amount ($)");
        etAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        final ResetPeriod[] periods = {
                ResetPeriod.MONTHLY, ResetPeriod.QUARTERLY,
                ResetPeriod.SEMI_ANNUALLY, ResetPeriod.ANNUALLY
        };
        final String[] periodLabels = {"Monthly", "Quarterly", "Semi-Annual", "Annual"};
        final int[] selPeriod = {3}; // default Annual

        RadioGroup rgPeriod = new RadioGroup(requireContext());
        rgPeriod.setOrientation(RadioGroup.VERTICAL);
        for (int i = 0; i < periodLabels.length; i++) {
            RadioButton rb = new RadioButton(requireContext());
            rb.setText(periodLabels[i]);
            rb.setId(View.generateViewId());
            final int idx = i;
            rb.setOnCheckedChangeListener((btn, checked) -> { if (checked) selPeriod[0] = idx; });
            rgPeriod.addView(rb);
        }
        rgPeriod.check(rgPeriod.getChildAt(3).getId());

        LinearLayout container = new LinearLayout(requireContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(padPx, padPx / 2, padPx, 0);

        TextView tvPeriodLabel = new TextView(requireContext());
        tvPeriodLabel.setText("Reset period");

        container.addView(etName);
        container.addView(etAmount);
        container.addView(tvPeriodLabel);
        container.addView(rgPeriod);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Credit / Benefit")
                .setView(container)
                .setPositiveButton("Add", (d, w) -> {
                    String name = etName.getText() != null
                            ? etName.getText().toString().trim() : "";
                    String amtStr = etAmount.getText() != null
                            ? etAmount.getText().toString().trim() : "";
                    if (name.isEmpty() || amtStr.isEmpty()) return;
                    try {
                        int amountCents = (int) (Double.parseDouble(amtStr) * 100);
                        AddCustomCardViewModel.PendingBenefit pb = new AddCustomCardViewModel.PendingBenefit();
                        pb.name = name;
                        pb.amountCents = amountCents;
                        pb.resetPeriod = periods[selPeriod[0]];
                        viewModel.addBenefit(pb);
                    } catch (NumberFormatException ignored) {}
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Welcome Bonus ─────────────────────────────────────────────────────────

    private void showWbBottomSheet() {
        String currency = currentCurrencyName();
        WelcomeBonus current = viewModel.getPendingWelcomeBonus();
        WelcomeBonusBottomSheet sheet = current != null
                ? WelcomeBonusBottomSheet.newInstance(current)
                : WelcomeBonusBottomSheet.newInstance(currency);
        sheet.show(getChildFragmentManager(), WelcomeBonusBottomSheet.TAG);
    }

    private void bindWbSummary(@Nullable WelcomeBonus wb) {
        if (wb == null) {
            binding.llWbSummaryCustom.setVisibility(View.GONE);
            binding.btnSetWbCustom.setVisibility(View.VISIBLE);
        } else {
            binding.llWbSummaryCustom.setVisibility(View.VISIBLE);
            binding.btnSetWbCustom.setVisibility(View.GONE);
            String bonus = WelcomeBonusBottomSheet.isCashBack(wb.bonusCurrencyName)
                    ? String.format(Locale.US, "$%.0f", wb.bonusPoints / 100.0)
                    : String.format(Locale.US, "%,d pts", wb.bonusPoints);
            binding.tvWbSummaryCustom.setText(bonus + " · Spend $" + (wb.spendRequirementCents / 100)
                    + (wb.deadline != null ? " · by " + wb.deadline : ""));
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private View makePendingRow(String label, View.OnClickListener onDelete) {
        View row = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_pending_row, binding.llPendingRates, false);
        ((TextView) row.findViewById(R.id.tv_pending_label)).setText(label);
        row.findViewById(R.id.btn_delete_pending).setOnClickListener(onDelete);
        return row;
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select open date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            LocalDate date = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.of("UTC")).toLocalDate();
            selectedDate.set(date);
            binding.etCustomOpenDate.setText(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        });
        picker.show(getChildFragmentManager(), "date_picker");
    }

    private String currentCurrencyName() {
        return selectedCardCurrency.isEmpty() ? "Cash Back" : selectedCardCurrency;
    }

    private String text(android.widget.EditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private String formatRate(double rate) {
        return rate == Math.floor(rate) ? String.valueOf((int) rate) : String.valueOf(rate);
    }

    private String rateTypeLabel(RateType type) {
        if (type == null) return "pts";
        switch (type) {
            case CASHBACK: return "cb";
            case MILES: return "mi";
            default: return "pts";
        }
    }

    private String periodLabel(ResetPeriod period) {
        if (period == null) return "yr";
        switch (period) {
            case MONTHLY: return "mo";
            case QUARTERLY: return "qtr";
            case SEMI_ANNUALLY: return "6mo";
            default: return "yr";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
