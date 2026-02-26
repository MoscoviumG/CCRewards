package com.example.ccrewards.ui.bestcard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.relations.BestCardForCategory;
import com.example.ccrewards.databinding.FragmentBestCardBinding;
import com.example.ccrewards.databinding.ItemCategoryTileBinding;
import com.example.ccrewards.util.CurrencyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BestCardFragment extends Fragment {

    // Display order for categories
    private static final RewardCategory[] CATEGORY_ORDER = {
            RewardCategory.DINING,
            RewardCategory.GROCERIES,
            RewardCategory.TRAVEL,
            RewardCategory.GAS,
            RewardCategory.ENTERTAINMENT,
            RewardCategory.ONLINE_SHOPPING,
            RewardCategory.RENT_MORTGAGE,
            RewardCategory.GENERAL
    };

    private static final String[] CATEGORY_LABELS = {
            "Dining", "Groceries", "Travel", "Gas",
            "Entertainment", "Online Shopping", "Rent / Mortgage", "General"
    };

    private FragmentBestCardBinding binding;
    private BestCardViewModel viewModel;
    private CategoryTileAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBestCardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BestCardViewModel.class);

        // Grid of 2 columns
        adapter = new CategoryTileAdapter();
        binding.recyclerCategories.setLayoutManager(
                new GridLayoutManager(requireContext(), 2));
        binding.recyclerCategories.setAdapter(adapter);

        adapter.setOnTileClickListener((category, label) -> {
            Bundle args = new Bundle();
            args.putString("categoryName", category.name());
            Navigation.findNavController(view)
                    .navigate(R.id.action_bestCard_to_categoryDetail, args);
        });

        // Observe ranked data
        viewModel.getRanked().observe(getViewLifecycleOwner(), ranked -> {
            List<CategoryTileAdapter.TileData> tiles = new ArrayList<>();
            for (int i = 0; i < CATEGORY_ORDER.length; i++) {
                RewardCategory cat = CATEGORY_ORDER[i];
                BestCardForCategory top = BestCardViewModel.getTop(ranked, cat);
                tiles.add(new CategoryTileAdapter.TileData(cat, CATEGORY_LABELS[i], top));
            }
            adapter.setTiles(tiles);
        });

        // Filter chips
        binding.bestCardFilterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_bc_all) viewModel.setFilter(BestCardViewModel.Filter.ALL_CARDS);
            else if (id == R.id.chip_bc_my_cards) viewModel.setFilter(BestCardViewModel.Filter.MY_CARDS);
            else if (id == R.id.chip_bc_personal) viewModel.setFilter(BestCardViewModel.Filter.PERSONAL);
            else if (id == R.id.chip_bc_business) viewModel.setFilter(BestCardViewModel.Filter.BUSINESS);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Tile Adapter ─────────────────────────────────────────────────────────

    private static class CategoryTileAdapter
            extends RecyclerView.Adapter<CategoryTileAdapter.VH> {

        static class TileData {
            final RewardCategory category;
            final String label;
            final BestCardForCategory top;

            TileData(RewardCategory category, String label, BestCardForCategory top) {
                this.category = category;
                this.label = label;
                this.top = top;
            }
        }

        interface OnTileClickListener {
            void onClick(RewardCategory category, String label);
        }

        private List<TileData> tiles = new ArrayList<>();
        private OnTileClickListener listener;

        void setTiles(List<TileData> data) {
            tiles = data != null ? data : new ArrayList<>();
            notifyDataSetChanged();
        }

        void setOnTileClickListener(OnTileClickListener l) { listener = l; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemCategoryTileBinding b = ItemCategoryTileBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            TileData data = tiles.get(position);
            holder.binding.tvCategoryName.setText(data.label);

            if (data.top != null) {
                holder.binding.tvBestCardName.setText(data.top.cardDisplayName);
                holder.binding.tvEffectiveReturn.setText(
                        CurrencyUtil.formatEffectiveReturn(data.top.effectiveReturn));
                holder.binding.chipYouOwn.setVisibility(
                        data.top.isUserOwned ? View.VISIBLE : View.GONE);
            } else {
                holder.binding.tvBestCardName.setText("No cards");
                holder.binding.tvEffectiveReturn.setText("—");
                holder.binding.chipYouOwn.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(data.category, data.label);
            });
        }

        @Override
        public int getItemCount() { return tiles.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final ItemCategoryTileBinding binding;
            VH(ItemCategoryTileBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}
