package com.example.ccrewards.ui.bestcard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.data.model.relations.BestCardForCategory;
import com.example.ccrewards.databinding.FragmentBestCardBinding;
import com.example.ccrewards.databinding.ItemCategoryTileBinding;
import com.example.ccrewards.databinding.ItemWelcomeBonusBannerBinding;
import com.example.ccrewards.ui.settings.SettingsFragment;
import com.example.ccrewards.util.CurrencyUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BestCardFragment extends Fragment {

    // Built-in display order — Travel is an umbrella tile (TRAVEL_PORTAL lives inside the group)
    private static final RewardCategory[] CATEGORY_ORDER = {
            RewardCategory.GENERAL,
            RewardCategory.DINING,
            RewardCategory.GROCERIES,
            RewardCategory.TRAVEL,
            RewardCategory.GAS,
            RewardCategory.ENTERTAINMENT,
            RewardCategory.ONLINE_SHOPPING,
            RewardCategory.RENT_MORTGAGE
    };

    private static final String[] CATEGORY_LABELS = {
            "General", "Dining", "Groceries", "Travel",
            "Gas", "Entertainment", "Online Shopping", "Rent / Mortgage"
    };

    private FragmentBestCardBinding binding;
    private BestCardViewModel viewModel;
    private CategoryTileAdapter adapter;
    private WelcomeBonusAdapter wbAdapter;

    // Last observed values for combining standard + custom tiles
    private Map<RewardCategory, List<BestCardForCategory>> latestRanked = null;
    private List<BestCardViewModel.CustomCategoryRanking> latestCustomRanked = null;
    // Display mode: true = effective return (8.20%) is primary; false = raw rate (4x UR) is primary
    private boolean showEffective = true;

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

        // Welcome bonus banner
        wbAdapter = new WelcomeBonusAdapter();
        binding.recyclerWelcomeBonuses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerWelcomeBonuses.setAdapter(wbAdapter);
        binding.recyclerWelcomeBonuses.setNestedScrollingEnabled(false);

        viewModel.getActiveBonuses().observe(getViewLifecycleOwner(), bonuses -> {
            boolean hasAny = bonuses != null && !bonuses.isEmpty();
            binding.sectionWelcomeBonuses.setVisibility(hasAny ? View.VISIBLE : View.GONE);
            wbAdapter.setData(bonuses != null ? bonuses : new ArrayList<>());
        });

        // Category tiles
        adapter = new CategoryTileAdapter();
        binding.recyclerCategories.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerCategories.setAdapter(adapter);

        adapter.setOnTileClickListener(tile -> {
            if (tile.isCustom) {
                Bundle args = new Bundle();
                args.putLong("customCategoryId", tile.customCategoryId);
                Navigation.findNavController(view)
                        .navigate(R.id.action_bestCard_to_customCategoryDetail, args);
            } else if (tile.category == RewardCategory.TRAVEL) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_bestCard_to_travelGroup);
            } else {
                Bundle args = new Bundle();
                args.putString("categoryName", tile.category.name());
                Navigation.findNavController(view)
                        .navigate(R.id.action_bestCard_to_categoryDetail, args);
            }
        });

        // Observe built-in rankings
        viewModel.getRanked().observe(getViewLifecycleOwner(), ranked -> {
            latestRanked = ranked;
            refreshTiles(view);
        });

        // Observe custom category rankings
        viewModel.getCustomRanked().observe(getViewLifecycleOwner(), customRanked -> {
            latestCustomRanked = customRanked;
            refreshTiles(view);
        });

        // FAB — add custom category
        binding.fabAddCustomCategory.setOnClickListener(v -> showAddCustomCategoryDialog(view));

        // My Cards filter switch — default on (MY_CARDS is the default filter)
        binding.switchMyCards.setChecked(true);
        binding.switchMyCards.setOnCheckedChangeListener((btn, checked) ->
                viewModel.setFilter(checked
                        ? BestCardViewModel.Filter.MY_CARDS
                        : BestCardViewModel.Filter.ALL_CARDS));

        // Display mode preference
        showEffective = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean(SettingsFragment.PREF_BEST_CARD_SHOW_EFFECTIVE, true);
        adapter.setShowEffective(showEffective);
        wbAdapter.setShowEffective(showEffective);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding == null) return;
        boolean current = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean(SettingsFragment.PREF_BEST_CARD_SHOW_EFFECTIVE, true);
        if (current != showEffective) {
            showEffective = current;
            adapter.setShowEffective(showEffective);
            adapter.notifyDataSetChanged();
            wbAdapter.setShowEffective(showEffective);
            wbAdapter.notifyDataSetChanged();
        }
    }

    private void refreshTiles(View view) {
        List<CategoryTileAdapter.TileData> tiles = new ArrayList<>();

        // Built-in categories
        if (latestRanked != null) {
            for (int i = 0; i < CATEGORY_ORDER.length; i++) {
                RewardCategory cat = CATEGORY_ORDER[i];
                BestCardForCategory top = BestCardViewModel.getTop(latestRanked, cat);
                tiles.add(new CategoryTileAdapter.TileData(cat, CATEGORY_LABELS[i], top));
            }
        }

        // Custom categories appended at end
        if (latestCustomRanked != null) {
            for (BestCardViewModel.CustomCategoryRanking ranking : latestCustomRanked) {
                BestCardForCategory top = ranking.rankings.isEmpty() ? null : ranking.rankings.get(0);
                tiles.add(new CategoryTileAdapter.TileData(
                        ranking.category.id, ranking.category.name, top));
            }
        }

        adapter.setTiles(tiles);
    }

    private void showAddCustomCategoryDialog(View navView) {
        EditText input = new EditText(requireContext());
        input.setHint("Category name (e.g., Apple.com)");
        input.setPadding(48, 24, 48, 0);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add Custom Category")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText() != null
                            ? input.getText().toString().trim() : "";
                    if (name.isEmpty()) return;
                    viewModel.getCustomCategoryRepository().insertCategory(name, newId -> {
                        requireActivity().runOnUiThread(() -> {
                            Bundle args = new Bundle();
                            args.putLong("customCategoryId", newId);
                            Navigation.findNavController(navView)
                                    .navigate(R.id.action_bestCard_to_customCategoryDetail, args);
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Rate formatting helpers ───────────────────────────────────────────────

    static String formatRawRate(BestCardForCategory top) {
        double rate = top.rate;
        switch (top.rateType) {
            case CASHBACK:
                return (rate == Math.floor(rate))
                        ? (int) rate + "%"
                        : String.format(java.util.Locale.US, "%.1f%%", rate);
            case BILT_CASH:
                return (rate == Math.floor(rate))
                        ? (int) rate + "x Bilt"
                        : String.format(java.util.Locale.US, "%.1fx Bilt", rate);
            case MILES:
            case POINTS:
            default:
                String rateStr = (rate == Math.floor(rate))
                        ? (int) rate + "x"
                        : String.format(java.util.Locale.US, "%.1fx", rate);
                return rateStr + " " + shortCurrencyName(top.rewardCurrencyName);
        }
    }

    private static String shortCurrencyName(String name) {
        if (name == null || name.isEmpty()) return "pts";
        switch (name) {
            case "Chase Ultimate Rewards Points":  return "UR";
            case "Amex Membership Rewards Points": return "MR";
            case "Citi ThankYou Points":           return "TY";
            case "Capital One Miles":              return "CO";
            case "Bilt Points":                    return "Bilt";
            case "Atmos/Alaska Rewards Miles":     return "AS";
            case "Delta SkyMiles":                 return "DL";
            case "Southwest Rapid Rewards":        return "SW";
            case "United MileagePlus":             return "UA";
            case "AAdvantage Miles":               return "AA";
            case "Avios":                          return "Avios";
            case "Aeroplan Miles":                 return "AC";
            case "Hilton Honors Points":           return "Hilton";
            case "Marriott Bonvoy Points":         return "Bonvoy";
            case "World of Hyatt Points":          return "Hyatt";
            case "IHG One Rewards Points":         return "IHG";
            case "BofA Points":                    return "BofA";
            case "Flying Blue Miles":              return "FB";
            case "Free Spirit Points":             return "FS";
            default:                               return name;
        }
    }

    // ── Welcome Bonus Banner Adapter ─────────────────────────────────────────

    private class WelcomeBonusAdapter
            extends RecyclerView.Adapter<WelcomeBonusAdapter.VH> {

        private List<BestCardViewModel.ActiveWelcomeBonus> items = new ArrayList<>();
        private boolean showEffective = true;

        void setData(List<BestCardViewModel.ActiveWelcomeBonus> data) {
            items = data != null ? data : new ArrayList<>();
            notifyDataSetChanged();
        }

        void setShowEffective(boolean v) {
            showEffective = v;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemWelcomeBonusBannerBinding b = ItemWelcomeBonusBannerBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            BestCardViewModel.ActiveWelcomeBonus item = items.get(position);
            WelcomeBonus wb = item.bonus;

            holder.binding.wbColorStrip.setBackgroundColor((int) item.cardColorPrimary);
            holder.binding.tvWbbCardName.setText(item.cardName);

            // Raw bonus value (points or cash, without spend req)
            String rawBonusStr;
            if (isCashBackStatic(wb.bonusCurrencyName)) {
                rawBonusStr = CurrencyUtil.centsToString(wb.bonusPoints) + " Cash Back";
            } else {
                rawBonusStr = NumberFormat.getInstance(Locale.US).format(wb.bonusPoints)
                        + " " + shortCurrencyName(wb.bonusCurrencyName);
            }

            // Spend req + optional deadline (always shown in the secondary slot)
            String detailsLine = "Spend " + CurrencyUtil.centsToString(wb.spendRequirementCents);
            if (wb.deadline != null) {
                detailsLine += " · due " + com.example.ccrewards.util.DateUtil.toDisplayString(wb.deadline);
            }

            // Effective return string
            String effectiveStr = String.format(Locale.US, "~%.3f%% eff.", item.effectiveReturnPct);

            // Swap primary/secondary based on toggle — same pattern as CategoryTileAdapter
            String combined = showEffective
                    ? effectiveStr + " · " + rawBonusStr   // "~8.2% eff. · 60,000 UR"
                    : rawBonusStr + " · " + effectiveStr;  // "60,000 UR · ~8.2% eff."
            holder.binding.tvWbbEffectiveRate.setText(combined);
            holder.binding.tvWbbBonusLine.setText(detailsLine);

            holder.binding.btnWbbDone.setOnClickListener(v -> {
                viewModel.markBonusAchieved(wb.userCardId);
                Snackbar.make(binding.getRoot(), "Welcome bonus marked as achieved",
                        Snackbar.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            final ItemWelcomeBonusBannerBinding binding;
            VH(ItemWelcomeBonusBannerBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }

    private static boolean isCashBackStatic(String currencyName) {
        return currencyName != null && currencyName.toLowerCase(Locale.US).contains("cash");
    }

    // ── Tile Adapter ─────────────────────────────────────────────────────────

    static class CategoryTileAdapter extends RecyclerView.Adapter<CategoryTileAdapter.VH> {

        static class TileData {
            final RewardCategory category;   // non-null for built-in
            final long customCategoryId;     // valid only when isCustom=true
            final boolean isCustom;
            final String label;
            final BestCardForCategory top;

            // Built-in category tile
            TileData(RewardCategory category, String label, BestCardForCategory top) {
                this.category = category;
                this.customCategoryId = -1L;
                this.isCustom = false;
                this.label = label;
                this.top = top;
            }

            // Custom category tile
            TileData(long customCategoryId, String label, BestCardForCategory top) {
                this.category = null;
                this.customCategoryId = customCategoryId;
                this.isCustom = true;
                this.label = label;
                this.top = top;
            }
        }

        interface OnTileClickListener {
            void onClick(TileData tile);
        }

        private List<TileData> tiles = new ArrayList<>();
        private OnTileClickListener listener;
        private boolean showEffective = true;

        void setShowEffective(boolean showEffective) {
            this.showEffective = showEffective;
        }

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

                String rawRate = formatRawRate(data.top);
                String effectiveStr = CurrencyUtil.formatEffectiveReturn(data.top.effectiveReturn);
                boolean isCashback = data.top.rateType == RateType.CASHBACK;

                if (showEffective) {
                    holder.binding.tvEffectiveReturn.setText(effectiveStr);
                    if (!isCashback) {
                        holder.binding.tvSecondaryValue.setText(rawRate);
                        holder.binding.tvSecondaryValue.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvSecondaryValue.setVisibility(View.GONE);
                    }
                } else {
                    holder.binding.tvEffectiveReturn.setText(rawRate);
                    if (!isCashback) {
                        holder.binding.tvSecondaryValue.setText(effectiveStr);
                        holder.binding.tvSecondaryValue.setVisibility(View.VISIBLE);
                    } else {
                        holder.binding.tvSecondaryValue.setVisibility(View.GONE);
                    }
                }

            } else {
                holder.binding.tvBestCardName.setText("No cards");
                holder.binding.tvEffectiveReturn.setText("—");
                holder.binding.tvSecondaryValue.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(data);
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
