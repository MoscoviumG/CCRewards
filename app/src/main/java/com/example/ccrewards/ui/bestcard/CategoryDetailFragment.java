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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.relations.BestCardForCategory;
import com.example.ccrewards.databinding.FragmentCategoryDetailBinding;
import com.example.ccrewards.databinding.ItemRankedCardBinding;
import com.example.ccrewards.util.CurrencyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryDetailFragment extends Fragment {

    private FragmentCategoryDetailBinding binding;
    private BestCardViewModel viewModel;
    private String categoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCategoryDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Share the ViewModel from the parent BestCardFragment via the activity scope
        viewModel = new ViewModelProvider(requireActivity()).get(BestCardViewModel.class);

        categoryName = getArguments() != null ? getArguments().getString("categoryName") : null;
        binding.toolbar.setTitle(formatCategoryName(categoryName));
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        RankingsAdapter adapter = new RankingsAdapter();
        binding.recyclerCategoryRankings.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCategoryRankings.setAdapter(adapter);

        viewModel.getRanked().observe(getViewLifecycleOwner(), ranked -> {
            if (categoryName == null || ranked == null) return;
            try {
                RewardCategory cat = RewardCategory.valueOf(categoryName);
                List<BestCardForCategory> list = ranked.get(cat);
                adapter.setData(list != null ? list : new ArrayList<>());
            } catch (IllegalArgumentException ignored) {}
        });
    }

    private String formatCategoryName(String enumName) {
        if (enumName == null) return "Category";
        String[] parts = enumName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (sb.length() > 0) sb.append(" / ");
            sb.append(part.charAt(0)).append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Rankings Adapter ─────────────────────────────────────────────────────

    private static class RankingsAdapter extends RecyclerView.Adapter<RankingsAdapter.VH> {
        private List<BestCardForCategory> items = new ArrayList<>();

        void setData(List<BestCardForCategory> data) {
            items = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemRankedCardBinding b = ItemRankedCardBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            BestCardForCategory item = items.get(position);
            holder.binding.tvRank.setText(String.valueOf(position + 1));
            holder.binding.tvRankedCardName.setText(item.cardDisplayName);
            holder.binding.tvRankedReturn.setText(
                    CurrencyUtil.formatEffectiveReturn(item.effectiveReturn));
            holder.binding.chipRankedOwned.setVisibility(
                    item.isUserOwned ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final ItemRankedCardBinding binding;
            VH(ItemRankedCardBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}
