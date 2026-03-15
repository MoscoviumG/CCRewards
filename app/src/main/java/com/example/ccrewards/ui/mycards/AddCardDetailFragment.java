package com.example.ccrewards.ui.mycards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.data.seed.AnnualFreeNightSeedData;
import com.example.ccrewards.databinding.FragmentAddCardDetailBinding;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddCardDetailFragment extends Fragment {

    private FragmentAddCardDetailBinding binding;
    private AddCardDetailViewModel viewModel;

    private final ActivityResultLauncher<Intent> wbLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != Activity.RESULT_OK || result.getData() == null)
                    return;
                Intent data = result.getData();
                CardDefinition def = viewModel.getCardDef().getValue();
                WelcomeBonus wb = new WelcomeBonus();
                wb.bonusPoints = data.getIntExtra(SetWelcomeBonusActivity.EXTRA_BONUS, 0);
                wb.bonusCurrencyName = def != null ? def.rewardCurrencyName : "";
                wb.spendRequirementCents = data.getIntExtra(SetWelcomeBonusActivity.EXTRA_SPEND, 0);
                long epoch = data.getLongExtra(SetWelcomeBonusActivity.EXTRA_DEADLINE, -1L);
                wb.deadline = epoch == -1L ? null : java.time.LocalDate.ofEpochDay(epoch);
                wb.showInBestCard = data.getBooleanExtra(SetWelcomeBonusActivity.EXTRA_SHOW_BC, true);
                wb.cashbackCents = data.getIntExtra(SetWelcomeBonusActivity.EXTRA_CASHBACK, 0);
                String fnTypeKey = data.getStringExtra(SetWelcomeBonusActivity.EXTRA_FN_TYPE);
                wb.fnTypeKey = (fnTypeKey != null && !fnTypeKey.isEmpty()) ? fnTypeKey : null;
                wb.fnCount = data.getIntExtra(SetWelcomeBonusActivity.EXTRA_FN_COUNT, 1);
                viewModel.setPendingWelcomeBonus(wb);
                bindWbSummary(wb);
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddCardDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddCardDetailViewModel.class);

        String cardDefinitionId = getArguments() != null
                ? getArguments().getString("cardDefinitionId") : null;
        if (cardDefinitionId != null) {
            viewModel.loadCard(cardDefinitionId);
        }

        // Toolbar back navigation
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        // Date picker
        final AtomicReference<LocalDate> selectedDate = new AtomicReference<>(LocalDate.now());
        binding.etOpenDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        binding.etOpenDate.setOnClickListener(v -> showDatePicker(selectedDate));

        // Observe card definition → bind header
        viewModel.getCardDef().observe(getViewLifecycleOwner(), card -> {
            if (card == null) return;
            binding.toolbar.setTitle(card.displayName);
            binding.detailColorStrip.setBackgroundColor((int) card.cardColorPrimary);
            binding.tvDetailCardName.setText(card.displayName);
            binding.tvDetailIssuer.setText(card.issuer + " · " + card.network);
            binding.tvDetailFee.setText(card.annualFee == 0
                    ? "No Annual Fee" : "$" + card.annualFee + "/yr");
            binding.tvDetailCurrency.setText(card.rewardCurrencyName);
        });

        // Observe rates → inflate rows
        viewModel.getRates().observe(getViewLifecycleOwner(), rateList -> {
            binding.llRates.removeAllViews();
            if (rateList == null || rateList.isEmpty()) return;
            Map<String, String> rateMap = CardDetailViewModel.buildRateDisplay(rateList);
            for (Map.Entry<String, String> entry : rateMap.entrySet()) {
                addInfoRow(binding.llRates, entry.getKey(), entry.getValue());
            }
        });

        // Observe benefits → inflate rows (header shown only when non-empty)
        viewModel.getBenefits().observe(getViewLifecycleOwner(), benefitList -> {
            binding.llBenefits.removeAllViews();
            if (benefitList == null || benefitList.isEmpty()) {
                binding.tvBenefitsHeader.setVisibility(View.GONE);
                return;
            }
            binding.tvBenefitsHeader.setVisibility(View.VISIBLE);
            for (CardBenefit b : benefitList) {
                String value = "$" + (b.amountCents / 100) + " / " + formatPeriod(b.resetPeriod);
                addInfoRow(binding.llBenefits, b.name, value);
            }
        });

        // Observe card def → also show annual free night info
        viewModel.getCardDef().observe(getViewLifecycleOwner(), card -> {
            binding.llFreeNights.removeAllViews();
            if (card == null) { binding.tvFnHeader.setVisibility(View.GONE); return; }
            java.util.List<AnnualFreeNightSeedData.Entry> fnEntries = new java.util.ArrayList<>();
            for (AnnualFreeNightSeedData.Entry e : AnnualFreeNightSeedData.getEntries()) {
                if (e.cardDefinitionId.equals(card.id)) fnEntries.add(e);
            }
            if (fnEntries.isEmpty()) {
                binding.tvFnHeader.setVisibility(View.GONE);
            } else {
                binding.tvFnHeader.setVisibility(View.VISIBLE);
                for (AnnualFreeNightSeedData.Entry e : fnEntries) {
                    addInfoRow(binding.llFreeNights, "Annual", e.label);
                }
            }
        });

        // Add to My Cards
        binding.btnAddCard.setOnClickListener(v -> {
            String nickname = binding.etNickname.getText() != null
                    ? binding.etNickname.getText().toString().trim() : "";
            String lastFour = binding.etLastFour.getText() != null
                    ? binding.etLastFour.getText().toString().trim() : "";
            String limitStr = binding.etLimit.getText() != null
                    ? binding.etLimit.getText().toString().trim() : "";
            int creditLimit = limitStr.isEmpty() ? 0 : Integer.parseInt(limitStr);
            LocalDate openDate = selectedDate.get() != null ? selectedDate.get() : LocalDate.now();

            viewModel.addUserCard(nickname, lastFour, creditLimit, openDate, () ->
                    requireActivity().runOnUiThread(() -> {
                        NavController nav = Navigation.findNavController(requireView());
                        nav.popBackStack(R.id.myCardsFragment, false);
                    }));
        });

        // Welcome Bonus
        binding.btnSetWb.setOnClickListener(v -> launchWbActivity(null));
        binding.btnWbEdit.setOnClickListener(v ->
                launchWbActivity(viewModel.getPendingWelcomeBonus()));
        binding.btnWbClear.setOnClickListener(v -> {
            viewModel.clearPendingWelcomeBonus();
            bindWbSummary(null);
        });

        // Restore pending WB state if ViewModel survived config change
        bindWbSummary(viewModel.getPendingWelcomeBonus());
    }

    private void launchWbActivity(@Nullable WelcomeBonus existing) {
        CardDefinition def = viewModel.getCardDef().getValue();
        String currency = def != null ? def.rewardCurrencyName : "";
        Intent intent = new Intent(requireContext(), SetWelcomeBonusActivity.class);
        intent.putExtra(SetWelcomeBonusActivity.EXTRA_CURRENCY, currency);
        if (existing != null) {
            intent.putExtra(SetWelcomeBonusActivity.EXTRA_BONUS, existing.bonusPoints);
            intent.putExtra(SetWelcomeBonusActivity.EXTRA_SPEND, existing.spendRequirementCents);
            intent.putExtra(SetWelcomeBonusActivity.EXTRA_DEADLINE,
                    existing.deadline != null ? existing.deadline.toEpochDay() : -1L);
            intent.putExtra(SetWelcomeBonusActivity.EXTRA_SHOW_BC, existing.showInBestCard);
            intent.putExtra(SetWelcomeBonusActivity.EXTRA_CASHBACK, existing.cashbackCents);
            intent.putExtra(SetWelcomeBonusActivity.EXTRA_FN_TYPE, existing.fnTypeKey);
            intent.putExtra(SetWelcomeBonusActivity.EXTRA_FN_COUNT, existing.fnCount);
        }
        wbLauncher.launch(intent);
    }

    private void bindWbSummary(@Nullable WelcomeBonus wb) {
        if (wb == null) {
            binding.llWbSummary.setVisibility(View.GONE);
            binding.btnSetWb.setVisibility(View.VISIBLE);
        } else {
            binding.llWbSummary.setVisibility(View.VISIBLE);
            binding.btnSetWb.setVisibility(View.GONE);
            StringBuilder sb = new StringBuilder();
            if (wb.bonusPoints > 0) {
                if (SetWelcomeBonusActivity.isCashBack(wb.bonusCurrencyName)) {
                    sb.append(String.format(Locale.US, "$%.0f Cash Back", wb.bonusPoints / 100.0));
                } else {
                    sb.append(String.format(Locale.US, "%,d pts", wb.bonusPoints));
                }
            }
            if (wb.cashbackCents > 0) {
                if (sb.length() > 0) sb.append(" + ");
                sb.append(String.format(Locale.US, "$%.0f Cash", wb.cashbackCents / 100.0));
            }
            if (wb.fnTypeKey != null) {
                if (sb.length() > 0) sb.append(" + ");
                sb.append(wb.fnCount).append("× FN");
            }
            sb.append(" · Spend $").append(wb.spendRequirementCents / 100);
            if (wb.deadline != null) sb.append(" · by ").append(wb.deadline);
            binding.tvWbSummary.setText(sb.toString());
        }
    }

    private void addInfoRow(ViewGroup parent, String label, String value) {
        View row = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_info_row, parent, false);
        ((TextView) row.findViewById(R.id.tv_row_label)).setText(label);
        ((TextView) row.findViewById(R.id.tv_row_value)).setText(value);
        parent.addView(row);
    }

    private void showDatePicker(AtomicReference<LocalDate> ref) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select open date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            LocalDate date = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.of("UTC")).toLocalDate();
            ref.set(date);
            binding.etOpenDate.setText(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        });
        picker.show(getChildFragmentManager(), "date_picker");
    }

    private String formatPeriod(ResetPeriod period) {
        if (period == null) return "yr";
        switch (period) {
            case MONTHLY: return "mo";
            case QUARTERLY: return "qtr";
            case SEMI_ANNUALLY: return "6mo";
            case ANNUALLY: return "yr";
            default: return "yr";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
