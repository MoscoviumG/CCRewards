package com.example.ccrewards.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.FreeNightValuation;
import com.example.ccrewards.databinding.FragmentFreeNightValuationsBinding;
import com.example.ccrewards.databinding.ItemFnValuationBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FreeNightValuationsFragment extends Fragment {

    private FragmentFreeNightValuationsBinding binding;
    private FreeNightValuationsViewModel viewModel;
    private FnValuationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFreeNightValuationsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(FreeNightValuationsViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        adapter = new FnValuationAdapter();
        binding.recyclerFnValuations.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerFnValuations.setAdapter(adapter);

        viewModel.getValuations().observe(getViewLifecycleOwner(), valuations ->
                adapter.setData(valuations != null ? valuations : new ArrayList<>()));
    }

    private void showEditDialog(FreeNightValuation valuation) {
        int dpPad = (int) (16 * getResources().getDisplayMetrics().density);
        EditText et = new EditText(requireContext());
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        et.setPadding(dpPad, dpPad, dpPad, dpPad);
        et.setHint("Value in dollars (e.g. 175)");
        if (valuation.valueCents > 0) {
            et.setText(String.format(Locale.US, "%.0f", valuation.valueCents / 100.0));
        }

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(valuation.label)
                .setView(et)
                .setPositiveButton("Save", (dialog, which) -> {
                    String text = et.getText().toString().trim();
                    if (text.isEmpty()) return;
                    try {
                        int newCents = (int) (Double.parseDouble(text) * 100);
                        valuation.valueCents = newCents;
                        viewModel.updateValuation(valuation);
                    } catch (NumberFormatException ignored) {}
                })
                .setNeutralButton("Reset to Default", (dialog, which) ->
                        viewModel.resetToDefault(valuation.typeKey))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class FnValuationAdapter extends RecyclerView.Adapter<FnValuationAdapter.VH> {

        private List<FreeNightValuation> items = new ArrayList<>();

        void setData(List<FreeNightValuation> data) {
            items = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemFnValuationBinding b = ItemFnValuationBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            FreeNightValuation val = items.get(position);
            holder.binding.tvFnLabel.setText(val.label);
            holder.binding.tvFnValue.setText(val.valueCents > 0
                    ? String.format(Locale.US, "$%d", val.valueCents / 100)
                    : "—");
            holder.itemView.setOnClickListener(v -> showEditDialog(val));
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            final ItemFnValuationBinding binding;
            VH(ItemFnValuationBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}
