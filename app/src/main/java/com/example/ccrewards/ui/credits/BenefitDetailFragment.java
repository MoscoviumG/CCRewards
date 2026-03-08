package com.example.ccrewards.ui.credits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.model.ResetType;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.databinding.FragmentBenefitDetailBinding;
import com.example.ccrewards.databinding.ItemHistoryRecordBinding;
import com.example.ccrewards.util.CurrencyUtil;
import com.example.ccrewards.util.DateUtil;
import com.example.ccrewards.util.PeriodKeyUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import android.app.DatePickerDialog;
import android.widget.EditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BenefitDetailFragment extends Fragment {

    @Inject BenefitRepository benefitRepository;
    @Inject CardRepository cardRepository;

    private FragmentBenefitDetailBinding binding;
    private long userCardId;
    private long benefitId;
    private String loadedCardDefinitionId;
    private CardBenefit loadedBenefit;
    private UserCard loadedUserCard;
    private String currentPeriodKey;
    private boolean sliderChanging = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBenefitDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userCardId = getArguments() != null ? getArguments().getLong("userCardId", -1) : -1;
        benefitId  = getArguments() != null ? getArguments().getLong("benefitId",  -1) : -1;

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == com.example.ccrewards.R.id.action_edit_benefit) {
                navigateToEdit();
                return true;
            }
            return false;
        });

        // Usage history adapter — non-static so it can show edit dialog
        UsageHistoryAdapter historyAdapter = new UsageHistoryAdapter();
        binding.recyclerUsageHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUsageHistory.setAdapter(historyAdapter);
        binding.recyclerUsageHistory.setNestedScrollingEnabled(false);

        benefitRepository.getUsageHistoryForBenefit(benefitId).observe(
                getViewLifecycleOwner(), historyAdapter::setData);
        historyAdapter.setOnClickListener(this::showEditUsageDialog);

        binding.btnAddPastEntry.setOnClickListener(v -> showAddUsageDialog());

        // Load benefit + user card on background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            CardBenefit benefit = benefitRepository.getBenefitByIdSync(benefitId);
            if (benefit == null || !isAdded()) return;

            UserCard userCard = cardRepository.getUserCardByIdSync(userCardId);
            boolean isCustom = benefit.resetType == ResetType.CUSTOM
                    && benefit.customResetMonth != null && benefit.customResetDay != null;
            boolean isAnniv = !isCustom && benefit.resetType == ResetType.ANNIVERSARY
                    && userCard != null && userCard.openDate != null;
            String periodKey;
            int daysUntilReset;
            if (isCustom) {
                periodKey = PeriodKeyUtil.getCurrentCustomPeriodKey(
                        benefit.resetPeriod, benefit.customResetMonth, benefit.customResetDay);
                daysUntilReset = PeriodKeyUtil.daysUntilCustomReset(
                        benefit.resetPeriod, benefit.customResetMonth, benefit.customResetDay);
            } else if (isAnniv) {
                periodKey = PeriodKeyUtil.getCurrentAnniversaryPeriodKey(
                        benefit.resetPeriod, userCard.openDate);
                daysUntilReset = PeriodKeyUtil.daysUntilAnniversaryReset(
                        benefit.resetPeriod, userCard.openDate);
            } else {
                periodKey = PeriodKeyUtil.getCurrentPeriodKey(benefit.resetPeriod);
                daysUntilReset = PeriodKeyUtil.daysUntilReset(benefit.resetPeriod);
            }
            BenefitUsage currentUsage = benefitRepository.getUsageSync(userCardId, benefitId, periodKey);

            loadedBenefit = benefit;
            loadedUserCard = userCard;
            currentPeriodKey = periodKey;
            loadedCardDefinitionId = userCard != null ? userCard.cardDefinitionId : "";

            requireActivity().runOnUiThread(() -> {
                if (!isAdded() || binding == null) return;

                binding.toolbar.setTitle(benefit.name);
                binding.tvBdName.setText(benefit.name);
                binding.tvBdCardName.setText(userCard != null ? userCard.cardDefinitionId : "");
                binding.tvBdDescription.setText(benefit.description != null ? benefit.description : "");
                binding.tvBdAmount.setText(
                        benefit.amountCents > 0
                                ? CurrencyUtil.centsToString(benefit.amountCents)
                                : "Non-monetary");
                binding.chipBdPeriod.setText(formatPeriod(benefit.resetPeriod));
                binding.chipBdDays.setText(daysUntilReset + " days left");

                int usedCents = currentUsage != null ? currentUsage.usedCents : 0;

                if (benefit.amountCents > 0) {
                    // Slider mode
                    binding.layoutSliderSection.setVisibility(View.VISIBLE);
                    binding.layoutToggleSection.setVisibility(View.GONE);

                    float maxDollars = benefit.amountCents / 100f;
                    binding.sliderBdUsage.setValueTo(maxDollars);
                    binding.sliderBdUsage.setStepSize(1f);
                    float usedDollars = Math.min(usedCents / 100f, maxDollars);
                    binding.sliderBdUsage.setValue(usedDollars);
                    updateUsedLabel(usedCents, benefit.amountCents);

                    binding.sliderBdUsage.addOnChangeListener((slider, value, fromUser) -> {
                        if (!fromUser || sliderChanging) return;
                        int newUsedCents = Math.round(value * 100);
                        updateUsedLabel(newUsedCents, benefit.amountCents);
                        benefitRepository.setUsedAmount(
                                userCardId, benefitId, periodKey, newUsedCents, benefit.amountCents);
                    });

                    binding.btnBdMarkFull.setOnClickListener(v -> {
                        sliderChanging = true;
                        binding.sliderBdUsage.setValue(maxDollars);
                        sliderChanging = false;
                        updateUsedLabel(benefit.amountCents, benefit.amountCents);
                        benefitRepository.setUsedAmount(
                                userCardId, benefitId, periodKey,
                                benefit.amountCents, benefit.amountCents);
                    });

                } else {
                    // Toggle mode for non-monetary benefits
                    binding.layoutSliderSection.setVisibility(View.GONE);
                    binding.layoutToggleSection.setVisibility(View.VISIBLE);
                    binding.switchBdUsed.setOnCheckedChangeListener(null);
                    binding.switchBdUsed.setChecked(currentUsage != null && currentUsage.isUsed);
                    binding.switchBdUsed.setOnCheckedChangeListener((btn, checked) ->
                            benefitRepository.setUsed(userCardId, benefitId, periodKey, checked));
                }
            });
        });
    }

    private void updateUsedLabel(int usedCents, int totalCents) {
        if (binding == null) return;
        binding.tvBdUsedAmount.setText(
                "$" + (usedCents / 100) + " / $" + (totalCents / 100));
    }

    private void navigateToEdit() {
        if (loadedCardDefinitionId == null) return;
        Bundle args = new Bundle();
        args.putString("cardDefinitionId", loadedCardDefinitionId);
        args.putLong("benefitId", benefitId);
        Navigation.findNavController(requireView())
                .navigate(com.example.ccrewards.R.id.action_benefitDetail_to_addEditBenefit, args);
    }

    private String formatPeriod(ResetPeriod period) {
        switch (period) {
            case MONTHLY:       return "Monthly";
            case QUARTERLY:     return "Quarterly";
            case SEMI_ANNUALLY: return "Semi-Annual";
            default:            return "Annual";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Edit usage history dialog ─────────────────────────────────────────────

    private void showEditUsageDialog(BenefitUsage usage) {
        if (loadedBenefit == null) return;
        boolean monetary = loadedBenefit.amountCents > 0;

        android.widget.LinearLayout container = new android.widget.LinearLayout(requireContext());
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, 0);

        EditText etAmount = null;
        android.widget.Switch swUsed = null;

        if (monetary) {
            etAmount = new EditText(requireContext());
            etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etAmount.setHint("Amount used ($)");
            etAmount.setText(String.valueOf(usage.usedCents / 100));
            container.addView(etAmount);
        } else {
            swUsed = new android.widget.Switch(requireContext());
            swUsed.setText("Marked as used");
            swUsed.setChecked(usage.isUsed);
            container.addView(swUsed);
        }

        final EditText finalEtAmount = etAmount;
        final android.widget.Switch finalSwUsed = swUsed;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit: " + usage.periodKey)
                .setView(container)
                .setPositiveButton("Save", (d, w) -> {
                    if (monetary && finalEtAmount != null) {
                        String s = finalEtAmount.getText().toString().trim();
                        int cents = s.isEmpty() ? 0 : (int)(Double.parseDouble(s) * 100);
                        usage.usedCents = cents;
                        usage.isUsed = cents >= loadedBenefit.amountCents;
                    } else if (finalSwUsed != null) {
                        usage.isUsed = finalSwUsed.isChecked();
                    }
                    benefitRepository.updateUsage(usage);
                    // Immediately sync the current-period slider/toggle if this is the active period
                    if (usage.periodKey.equals(currentPeriodKey) && binding != null) {
                        if (monetary) {
                            float maxDollars = loadedBenefit.amountCents / 100f;
                            float newDollars = Math.min(usage.usedCents / 100f, maxDollars);
                            sliderChanging = true;
                            binding.sliderBdUsage.setValue(newDollars);
                            sliderChanging = false;
                            updateUsedLabel(usage.usedCents, loadedBenefit.amountCents);
                        } else {
                            binding.switchBdUsed.setOnCheckedChangeListener(null);
                            binding.switchBdUsed.setChecked(usage.isUsed);
                            binding.switchBdUsed.setOnCheckedChangeListener((btn, checked) ->
                                    benefitRepository.setUsed(userCardId, benefitId, currentPeriodKey, checked));
                        }
                    }
                })
                .setNeutralButton("Delete", (d, w) ->
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Delete entry")
                                .setMessage("Remove usage record for " + usage.periodKey + "?")
                                .setPositiveButton("Delete", (d2, w2) -> benefitRepository.deleteUsage(usage))
                                .setNegativeButton("Cancel", null)
                                .show())
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Add past usage entry ──────────────────────────────────────────────────

    private void showAddUsageDialog() {
        if (loadedBenefit == null) return;
        LocalDate today = LocalDate.now();
        new DatePickerDialog(requireContext(), (picker, year, month0, dayOfMonth) -> {
            LocalDate picked = LocalDate.of(year, month0 + 1, dayOfMonth);
            String periodKey = computePeriodKeyForDate(picked);
            if (periodKey == null) return;
            showUsageAmountDialog(periodKey);
        }, today.getYear(), today.getMonthValue() - 1, today.getDayOfMonth()).show();
    }

    private String computePeriodKeyForDate(LocalDate date) {
        if (loadedBenefit == null) return null;
        boolean isCustom = loadedBenefit.resetType == ResetType.CUSTOM
                && loadedBenefit.customResetMonth != null && loadedBenefit.customResetDay != null;
        boolean isAnniv = !isCustom && loadedBenefit.resetType == ResetType.ANNIVERSARY
                && loadedUserCard != null && loadedUserCard.openDate != null;
        if (isCustom) {
            return PeriodKeyUtil.getCustomPeriodKey(
                    date, loadedBenefit.resetPeriod,
                    loadedBenefit.customResetMonth, loadedBenefit.customResetDay);
        } else if (isAnniv) {
            return PeriodKeyUtil.getAnniversaryPeriodKey(
                    date, loadedBenefit.resetPeriod, loadedUserCard.openDate);
        } else {
            return PeriodKeyUtil.getPeriodKey(date, loadedBenefit.resetPeriod);
        }
    }

    private void showUsageAmountDialog(String periodKey) {
        if (loadedBenefit == null) return;
        boolean monetary = loadedBenefit.amountCents > 0;

        android.widget.LinearLayout container = new android.widget.LinearLayout(requireContext());
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, 0);

        EditText etAmount = null;
        android.widget.Switch swUsed = null;

        if (monetary) {
            etAmount = new EditText(requireContext());
            etAmount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
            etAmount.setHint("Amount used ($)");
            container.addView(etAmount);
        } else {
            swUsed = new android.widget.Switch(requireContext());
            swUsed.setText("Marked as used");
            swUsed.setChecked(false);
            container.addView(swUsed);
        }

        final EditText finalEt = etAmount;
        final android.widget.Switch finalSw = swUsed;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Entry: " + periodKey)
                .setView(container)
                .setPositiveButton("Save", (d, w) -> {
                    if (monetary && finalEt != null) {
                        String s = finalEt.getText().toString().trim();
                        int cents = s.isEmpty() ? 0 : (int)(Double.parseDouble(s) * 100);
                        benefitRepository.setUsedAmount(
                                userCardId, benefitId, periodKey, cents, loadedBenefit.amountCents);
                    } else if (finalSw != null) {
                        benefitRepository.setUsed(
                                userCardId, benefitId, periodKey, finalSw.isChecked());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Usage History Adapter ────────────────────────────────────────────────

    interface OnUsageClickListener {
        void onClick(BenefitUsage usage);
    }

    private class UsageHistoryAdapter extends RecyclerView.Adapter<UsageHistoryAdapter.VH> {
        private List<BenefitUsage> items = new ArrayList<>();
        private OnUsageClickListener clickListener;

        void setOnClickListener(OnUsageClickListener l) { clickListener = l; }

        void setData(List<BenefitUsage> data) {
            items = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHistoryRecordBinding b = ItemHistoryRecordBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            BenefitUsage usage = items.get(position);
            holder.binding.tvHistoryDate.setText(usage.periodKey);
            String status;
            if (usage.isUsed) {
                status = "Fully used";
            } else if (usage.usedCents > 0) {
                status = "$" + (usage.usedCents / 100) + " used";
            } else {
                status = "Not used";
            }
            if (usage.usedDate != null) {
                status += " · " + DateUtil.toDisplayString(usage.usedDate);
            }
            holder.binding.tvHistoryDescription.setText(status);
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onClick(usage);
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            final ItemHistoryRecordBinding binding;
            VH(ItemHistoryRecordBinding b) { super(b.getRoot()); binding = b; }
        }
    }
}
