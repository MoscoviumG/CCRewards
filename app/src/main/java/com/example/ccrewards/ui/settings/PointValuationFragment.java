package com.example.ccrewards.ui.settings;

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

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.databinding.FragmentPointValuationBinding;
import com.example.ccrewards.databinding.ItemPointValuationBinding;
import com.example.ccrewards.util.CurrencyUtil;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PointValuationFragment extends Fragment {

    private FragmentPointValuationBinding binding;
    private PointValuationViewModel viewModel;
    private ValuationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPointValuationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PointValuationViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        adapter = new ValuationAdapter();
        binding.recyclerValuations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerValuations.setAdapter(adapter);

        adapter.setOnClickListener(valuation -> {
            Bundle args = new Bundle();
            args.putString("currencyName", valuation.rewardCurrencyName);
            Navigation.findNavController(view)
                    .navigate(R.id.action_pointValuation_to_currencyDetail, args);
        });

        viewModel.getAllValuations().observe(getViewLifecycleOwner(), adapter::setData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Valuation Adapter ────────────────────────────────────────────────────

    private static class ValuationAdapter extends RecyclerView.Adapter<ValuationAdapter.VH> {
        interface OnClickListener { void onClick(PointValuation v); }

        private List<PointValuation> items = new ArrayList<>();
        private OnClickListener listener;

        void setData(List<PointValuation> data) {
            items = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        void setOnClickListener(OnClickListener l) { listener = l; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemPointValuationBinding b = ItemPointValuationBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            PointValuation v = items.get(position);
            holder.binding.tvCurrencyName.setText(v.rewardCurrencyName);
            holder.binding.tvCppValue.setText(CurrencyUtil.formatCentsPerPoint(v.centsPerPoint));
            holder.itemView.setOnClickListener(vw -> {
                if (listener != null) listener.onClick(v);
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final ItemPointValuationBinding binding;
            VH(ItemPointValuationBinding b) { super(b.getRoot()); binding = b; }
        }
    }
}
