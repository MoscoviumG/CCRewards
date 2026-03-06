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

        // Usage history adapter
        UsageHistoryAdapter historyAdapter = new UsageHistoryAdapter();
        binding.recyclerUsageHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUsageHistory.setAdapter(historyAdapter);
        binding.recyclerUsageHistory.setNestedScrollingEnabled(false);

        benefitRepository.getUsageHistoryForBenefit(benefitId).observe(
                getViewLifecycleOwner(), historyAdapter::setData);

        // Load benefit + user card on background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            CardBenefit benefit = benefitRepository.getBenefitByIdSync(benefitId);
            if (benefit == null || !isAdded()) return;

            UserCard userCard = cardRepository.getUserCardByIdSync(userCardId);
            boolean isAnniv = benefit.resetType == ResetType.ANNIVERSARY
                    && userCard != null && userCard.openDate != null;
            String periodKey = isAnniv
                    ? PeriodKeyUtil.getCurrentAnniversaryPeriodKey(benefit.resetPeriod, userCard.openDate)
                    : PeriodKeyUtil.getCurrentPeriodKey(benefit.resetPeriod);
            int daysUntilReset = isAnniv
                    ? PeriodKeyUtil.daysUntilAnniversaryReset(benefit.resetPeriod, userCard.openDate)
                    : PeriodKeyUtil.daysUntilReset(benefit.resetPeriod);
            BenefitUsage currentUsage = benefitRepository.getUsageSync(userCardId, benefitId, periodKey);
            String cardDefId = userCard != null ? userCard.cardDefinitionId : "";

            loadedCardDefinitionId = cardDefId;

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

    // ── Usage History Adapter ────────────────────────────────────────────────

    private static class UsageHistoryAdapter extends RecyclerView.Adapter<UsageHistoryAdapter.VH> {
        private List<BenefitUsage> items = new ArrayList<>();

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
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final ItemHistoryRecordBinding binding;
            VH(ItemHistoryRecordBinding b) { super(b.getRoot()); binding = b; }
        }
    }
}
