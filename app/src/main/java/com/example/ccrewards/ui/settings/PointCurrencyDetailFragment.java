package com.example.ccrewards.ui.settings;

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

import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.data.model.TransferPartner;
import com.example.ccrewards.databinding.FragmentPointCurrencyDetailBinding;
import com.example.ccrewards.databinding.ItemTransferPartnerBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PointCurrencyDetailFragment extends Fragment {

    private FragmentPointCurrencyDetailBinding binding;
    private PointValuationViewModel viewModel;
    private String currencyName;
    private PointValuation currentValuation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPointCurrencyDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PointValuationViewModel.class);

        currencyName = getArguments() != null ? getArguments().getString("currencyName") : null;
        binding.toolbar.setTitle(currencyName != null ? currencyName : "Currency");
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        // Observe all valuations to find the current one
        viewModel.getAllValuations().observe(getViewLifecycleOwner(), valuations -> {
            if (valuations == null || currencyName == null) return;
            for (PointValuation v : valuations) {
                if (currencyName.equals(v.rewardCurrencyName)) {
                    currentValuation = v;
                    binding.etCpp.setText(String.valueOf(v.centsPerPoint));
                    break;
                }
            }
        });

        // Save button
        binding.btnSaveCpp.setOnClickListener(v -> {
            String txt = binding.etCpp.getText() != null
                    ? binding.etCpp.getText().toString().trim() : "";
            if (txt.isEmpty()) return;
            try {
                double cpp = Double.parseDouble(txt);
                if (currentValuation != null) {
                    currentValuation.centsPerPoint = cpp;
                    viewModel.updateValuation(currentValuation);
                    Snackbar.make(v, "Valuation saved", Snackbar.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Snackbar.make(v, "Invalid value", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Reset button
        binding.btnResetCpp.setOnClickListener(v -> {
            if (currencyName != null) {
                viewModel.resetValuationToDefault(currencyName);
                Snackbar.make(v, "Reset to default", Snackbar.LENGTH_SHORT).show();
            }
        });

        // Airlines transfer partners
        TransferPartnerAdapter airlinesAdapter = new TransferPartnerAdapter();
        binding.recyclerAirlines.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerAirlines.setAdapter(airlinesAdapter);
        binding.recyclerAirlines.setNestedScrollingEnabled(false);

        // Hotels transfer partners
        TransferPartnerAdapter hotelsAdapter = new TransferPartnerAdapter();
        binding.recyclerHotels.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHotels.setAdapter(hotelsAdapter);
        binding.recyclerHotels.setNestedScrollingEnabled(false);

        if (currencyName != null) {
            viewModel.getAirlinePartners(currencyName).observe(
                    getViewLifecycleOwner(), airlinesAdapter::setData);
            viewModel.getHotelPartners(currencyName).observe(
                    getViewLifecycleOwner(), hotelsAdapter::setData);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Transfer Partner Adapter ─────────────────────────────────────────────

    private static class TransferPartnerAdapter extends RecyclerView.Adapter<TransferPartnerAdapter.VH> {
        private List<TransferPartner> items = new ArrayList<>();

        void setData(List<TransferPartner> data) {
            items = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTransferPartnerBinding b = ItemTransferPartnerBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            TransferPartner p = items.get(position);
            holder.binding.tvPartnerName.setText(p.partnerName);
            // Format ratio: "1:1.0" → show as "1:1" or "1:0.8"
            String ratio;
            if (p.ratioFrom == 1 && p.ratioTo == 1.0) {
                ratio = "1:1";
            } else {
                ratio = p.ratioFrom + ":" + (p.ratioTo == Math.floor(p.ratioTo)
                        ? (int) p.ratioTo : p.ratioTo);
            }
            holder.binding.tvTransferRatio.setText(ratio);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final ItemTransferPartnerBinding binding;
            VH(ItemTransferPartnerBinding b) { super(b.getRoot()); binding = b; }
        }
    }
}
