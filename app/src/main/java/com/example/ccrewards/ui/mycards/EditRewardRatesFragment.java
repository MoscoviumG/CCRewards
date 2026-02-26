package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.databinding.FragmentEditRewardRatesBinding;
import com.example.ccrewards.databinding.ItemRewardRateEditBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditRewardRatesFragment extends Fragment {

    private FragmentEditRewardRatesBinding binding;
    private EditRewardRatesViewModel viewModel;
    private RateEditAdapter adapter;
    private long userCardId;
    private String cardDefinitionId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEditRewardRatesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditRewardRatesViewModel.class);

        userCardId = getArguments() != null ? getArguments().getLong("userCardId", -1) : -1;
        cardDefinitionId = getArguments() != null ? getArguments().getString("cardDefinitionId") : null;

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        // RecyclerView
        adapter = new RateEditAdapter();
        binding.recyclerEditRates.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerEditRates.setAdapter(adapter);
        binding.recyclerEditRates.setNestedScrollingEnabled(false);

        if (cardDefinitionId != null) {
            viewModel.loadRates(cardDefinitionId);
        }

        viewModel.getRates().observe(getViewLifecycleOwner(), rates -> adapter.setRates(rates));

        // Save FAB
        binding.fabSaveRates.setOnClickListener(v -> {
            List<RewardRate> edited = adapter.getEditedRates();
            for (RewardRate rate : edited) {
                viewModel.saveRate(rate);
            }
            Navigation.findNavController(v).navigateUp();
        });

        // Reset button
        binding.btnResetRates.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Reset Rates")
                        .setMessage("Reset all reward rates to their default values?")
                        .setPositiveButton("Reset", (dialog, which) -> {
                            viewModel.resetAllCustomizations();
                            Navigation.findNavController(requireView()).navigateUp();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Rate Edit Adapter ────────────────────────────────────────────────────

    private static class RateEditAdapter extends RecyclerView.Adapter<RateEditAdapter.VH> {

        private List<RewardRate> rates = new ArrayList<>();

        void setRates(List<RewardRate> data) {
            rates = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        List<RewardRate> getEditedRates() {
            return new ArrayList<>(rates);
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemRewardRateEditBinding b = ItemRewardRateEditBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            RewardRate rate = rates.get(position);
            holder.bind(rate, position);
        }

        @Override
        public int getItemCount() { return rates.size(); }

        class VH extends RecyclerView.ViewHolder {
            final ItemRewardRateEditBinding binding;

            VH(ItemRewardRateEditBinding b) {
                super(b.getRoot());
                binding = b;
            }

            void bind(RewardRate rate, int position) {
                // Category label
                binding.tvEditCategory.setText(formatCategory(rate.category.name()));

                // Rate type label
                binding.tvRateTypeLabel.setText(getRateTypeSuffix(rate.rateType));

                // Rate value (editable)
                String rateStr = rate.rate == Math.floor(rate.rate)
                        ? String.valueOf((int) rate.rate)
                        : String.valueOf(rate.rate);
                binding.etEditRate.setText(rateStr);

                // Customized indicator
                binding.tvCustomizedLabel.setVisibility(rate.isCustomized ? View.VISIBLE : View.GONE);

                // Choice category chip
                binding.chipChoiceCategory.setVisibility(rate.isChoiceCategory ? View.VISIBLE : View.GONE);

                // Update model on text change
                binding.etEditRate.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                    @Override public void afterTextChanged(Editable s) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            double val = Double.parseDouble(s.toString());
                            rates.get(position).rate = val;
                        } catch (NumberFormatException ignored) {}
                    }
                });
            }

            private String formatCategory(String enumName) {
                String[] parts = enumName.split("_");
                StringBuilder sb = new StringBuilder();
                for (String part : parts) {
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(part.charAt(0)).append(part.substring(1).toLowerCase());
                }
                return sb.toString();
            }

            private String getRateTypeSuffix(com.example.ccrewards.data.model.RateType type) {
                switch (type) {
                    case CASHBACK: return "% Cash Back";
                    case BILT_CASH: return "% Bilt Cash";
                    case MILES: return "x Miles";
                    case POINTS: default: return "x Points";
                }
            }
        }
    }
}
