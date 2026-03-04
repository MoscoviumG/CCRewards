package com.example.ccrewards.ui.bestcard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.CustomCategory;
import com.example.ccrewards.data.model.CustomCategoryRate;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.databinding.FragmentCustomCategoryDetailBinding;
import com.example.ccrewards.databinding.ItemCustomCategoryRateBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CustomCategoryDetailFragment extends Fragment {

    private FragmentCustomCategoryDetailBinding binding;
    private CustomCategoryDetailViewModel viewModel;
    private long customCategoryId;
    private CardRateAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomCategoryDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CustomCategoryDetailViewModel.class);

        customCategoryId = getArguments() != null
                ? getArguments().getLong("customCategoryId", -1L) : -1L;

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == com.example.ccrewards.R.id.action_delete_custom_category) {
                showDeleteConfirmDialog(view);
                return true;
            }
            return false;
        });

        adapter = new CardRateAdapter();
        binding.recyclerCardRates.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCardRates.setAdapter(adapter);

        viewModel.load(customCategoryId);

        viewModel.getCategory().observe(getViewLifecycleOwner(), category -> {
            if (category != null && !binding.etCategoryName.isFocused()) {
                binding.etCategoryName.setText(category.name);
            }
        });

        viewModel.getCardRows().observe(getViewLifecycleOwner(), rows -> {
            adapter.setRows(rows);
        });

        binding.fabSaveCustomCategory.setOnClickListener(v -> saveAndNavigateUp(view));
    }

    private void saveAndNavigateUp(View navView) {
        String name = binding.etCategoryName.getText() != null
                ? binding.etCategoryName.getText().toString().trim() : "";
        if (name.isEmpty()) {
            Snackbar.make(navView, "Please enter a category name", Snackbar.LENGTH_SHORT).show();
            return;
        }
        viewModel.save(customCategoryId, name, adapter.getDirtyRates(), adapter.getClearedCardIds(),
                () -> requireActivity().runOnUiThread(() ->
                        Navigation.findNavController(navView).navigateUp()));
    }

    private void showDeleteConfirmDialog(View navView) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Category")
                .setMessage("Delete this custom category and all its rate overrides?")
                .setPositiveButton("Delete", (dialog, which) ->
                        viewModel.delete(customCategoryId, () ->
                                requireActivity().runOnUiThread(() ->
                                        Navigation.findNavController(navView).navigateUp())))
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Adapter ──────────────────────────────────────────────────────────────

    static class CardRateAdapter extends RecyclerView.Adapter<CardRateAdapter.VH> {

        static class CardRateRow {
            final CardDefinition card;
            final String generalFallbackLabel;  // e.g. "General: 1x Points"
            @Nullable CustomCategoryRate existingOverride;  // null = no override
            String pendingRateText;  // current text in the EditText (may be dirty)
            boolean cleared;         // user tapped X to remove override

            CardRateRow(CardDefinition card, String generalFallbackLabel,
                        @Nullable CustomCategoryRate existingOverride) {
                this.card = card;
                this.generalFallbackLabel = generalFallbackLabel;
                this.existingOverride = existingOverride;
                this.pendingRateText = existingOverride != null
                        ? String.valueOf(existingOverride.rate) : "";
                this.cleared = false;
            }
        }

        private List<CardRateRow> rows = new ArrayList<>();

        void setRows(List<CardRateRow> data) {
            rows = data != null ? data : new ArrayList<>();
            notifyDataSetChanged();
        }

        /** Returns rows where the user has entered a rate (and not cleared it). */
        List<CardRateRow> getDirtyRates() {
            List<CardRateRow> dirty = new ArrayList<>();
            for (CardRateRow row : rows) {
                if (!row.cleared && !row.pendingRateText.isEmpty()) {
                    dirty.add(row);
                }
            }
            return dirty;
        }

        /** Returns card IDs whose overrides have been explicitly cleared. */
        List<String> getClearedCardIds() {
            List<String> cleared = new ArrayList<>();
            for (CardRateRow row : rows) {
                if (row.cleared) cleared.add(row.card.id);
            }
            return cleared;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemCustomCategoryRateBinding b = ItemCustomCategoryRateBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CardRateRow row = rows.get(position);
            holder.binding.tvCardName.setText(row.card.displayName);
            holder.binding.tvGeneralFallback.setText(row.generalFallbackLabel);

            // Remove old watcher before setText so it doesn't fire spuriously
            if (holder.watcher != null) {
                holder.binding.etCustomRate.removeTextChangedListener(holder.watcher);
            }
            holder.binding.etCustomRate.setText(row.pendingRateText);

            // Show/hide clear button
            boolean hasOverride = (row.existingOverride != null || !row.pendingRateText.isEmpty())
                    && !row.cleared;
            holder.binding.btnClearRate.setVisibility(hasOverride ? View.VISIBLE : View.GONE);

            // Create watcher and register it exactly once
            holder.watcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
                @Override
                public void afterTextChanged(Editable s) {
                    int pos = holder.getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        rows.get(pos).pendingRateText = s.toString();
                        rows.get(pos).cleared = false;
                    }
                }
            };
            holder.binding.etCustomRate.addTextChangedListener(holder.watcher);

            holder.binding.btnClearRate.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                CardRateRow r = rows.get(pos);
                r.cleared = true;
                r.pendingRateText = "";
                notifyItemChanged(pos);
            });
        }

        @Override
        public int getItemCount() { return rows.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final ItemCustomCategoryRateBinding binding;
            TextWatcher watcher;

            VH(ItemCustomCategoryRateBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}
