package com.example.ccrewards.ui.credits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.BenefitUsage;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.ResetPeriod;
import com.example.ccrewards.data.repository.BenefitRepository;
import com.example.ccrewards.databinding.FragmentBenefitDetailBinding;
import com.example.ccrewards.databinding.ItemHistoryRecordBinding;
import com.example.ccrewards.util.CurrencyUtil;
import com.example.ccrewards.util.DateUtil;
import com.example.ccrewards.util.PeriodKeyUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BenefitDetailFragment extends Fragment {

    @Inject
    BenefitRepository benefitRepository;

    private FragmentBenefitDetailBinding binding;
    private long userCardId;
    private long benefitId;

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
        benefitId = getArguments() != null ? getArguments().getLong("benefitId", -1) : -1;

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        // Load benefit details
        benefitRepository.getBenefitsForCard("").observeForever(benefits -> {}); // trigger Room init
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            CardBenefit benefit = benefitRepository.getBenefitByIdSync(benefitId);
            if (benefit == null) return;

            String periodKey = PeriodKeyUtil.getCurrentPeriodKey(benefit.resetPeriod);
            BenefitUsage currentUsage = benefitRepository.getUsageSync(userCardId, benefitId, periodKey);

            requireActivity().runOnUiThread(() -> {
                binding.toolbar.setTitle(benefit.name);
                binding.tvBdName.setText(benefit.name);
                binding.tvBdDescription.setText(benefit.description != null ? benefit.description : "");
                binding.tvBdAmount.setText(CurrencyUtil.centsToString(benefit.amountCents));
                binding.chipBdPeriod.setText(formatPeriod(benefit.resetPeriod));

                binding.switchBdUsed.setChecked(currentUsage != null && currentUsage.isUsed);
                binding.switchBdUsed.setOnCheckedChangeListener((btn, checked) -> {
                    benefitRepository.setUsed(userCardId, benefitId, periodKey, checked);
                });
            });
        });

        // Usage history
        UsageHistoryAdapter historyAdapter = new UsageHistoryAdapter();
        binding.recyclerUsageHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUsageHistory.setAdapter(historyAdapter);
        binding.recyclerUsageHistory.setNestedScrollingEnabled(false);

        benefitRepository.getUsageHistoryForBenefit(benefitId).observe(
                getViewLifecycleOwner(), historyAdapter::setData);
    }

    private String formatPeriod(ResetPeriod period) {
        switch (period) {
            case MONTHLY: return "Monthly";
            case QUARTERLY: return "Quarterly";
            case SEMI_ANNUALLY: return "Semi-Annual";
            default: return "Annual";
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
            holder.binding.tvHistoryDescription.setText(
                    usage.isUsed ? "Used" + (usage.usedDate != null
                            ? " on " + DateUtil.toDisplayString(usage.usedDate) : "")
                            : "Not used");
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final ItemHistoryRecordBinding binding;
            VH(ItemHistoryRecordBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}
