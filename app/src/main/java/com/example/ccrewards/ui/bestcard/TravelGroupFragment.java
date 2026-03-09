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

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.relations.BestCardForCategory;
import com.example.ccrewards.databinding.FragmentTravelGroupBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TravelGroupFragment extends Fragment {

    // Ordered list of travel sub-categories shown in this screen
    private static final RewardCategory[] TRAVEL_SUBCATEGORIES = {
            RewardCategory.TRAVEL,
            RewardCategory.TRAVEL_PORTAL,
            // Airlines (alphabetical)
            RewardCategory.TRAVEL_ALASKA,
            RewardCategory.TRAVEL_AA,
            RewardCategory.TRAVEL_AER_LINGUS,
            RewardCategory.TRAVEL_AIR_FRANCE_KLM,
            RewardCategory.TRAVEL_AEROPLAN,
            RewardCategory.TRAVEL_ALLEGIANT,
            RewardCategory.TRAVEL_BRITISH_AIRWAYS,
            RewardCategory.TRAVEL_DELTA,
            RewardCategory.TRAVEL_EMIRATES,
            RewardCategory.TRAVEL_FRONTIER,
            RewardCategory.TRAVEL_HAWAIIAN,
            RewardCategory.TRAVEL_IBERIA,
            RewardCategory.TRAVEL_JETBLUE,
            RewardCategory.TRAVEL_LUFTHANSA,
            RewardCategory.TRAVEL_SOUTHWEST,
            RewardCategory.TRAVEL_SPIRIT,
            RewardCategory.TRAVEL_UNITED,
            // Hotels (alphabetical)
            RewardCategory.TRAVEL_HILTON,
            RewardCategory.TRAVEL_HYATT,
            RewardCategory.TRAVEL_IHG,
            RewardCategory.TRAVEL_MARRIOTT,
            RewardCategory.TRAVEL_WYNDHAM,
            // Other
            RewardCategory.TRAVEL_CRUISES
    };

    private static final String[] TRAVEL_SUBCATEGORY_LABELS = {
            "General Travel",
            "Travel Portal",
            // Airlines
            "Alaska Airlines",
            "American Airlines",
            "Aer Lingus",
            "Air France / KLM",
            "Aeroplan",
            "Allegiant",
            "British Airways",
            "Delta",
            "Emirates",
            "Frontier",
            "Hawaiian Airlines",
            "Iberia",
            "JetBlue",
            "Lufthansa",
            "Southwest",
            "Spirit",
            "United",
            // Hotels
            "Hilton",
            "Hyatt",
            "IHG",
            "Marriott",
            "Wyndham",
            // Other
            "Cruises"
    };

    private FragmentTravelGroupBinding binding;
    private BestCardViewModel viewModel;
    private BestCardFragment.CategoryTileAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTravelGroupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(BestCardViewModel.class);

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        adapter = new BestCardFragment.CategoryTileAdapter();
        binding.recyclerTravelSubcategories.setLayoutManager(
                new GridLayoutManager(requireContext(), 2));
        binding.recyclerTravelSubcategories.setAdapter(adapter);

        adapter.setOnTileClickListener(tile -> {
            Bundle args = new Bundle();
            args.putString("categoryName", tile.category.name());
            Navigation.findNavController(view)
                    .navigate(R.id.action_travelGroup_to_categoryDetail, args);
        });

        viewModel.getRanked().observe(getViewLifecycleOwner(), ranked -> {
            if (ranked == null) return;
            refreshTiles(ranked);
        });

        // Respect current show-effective preference
        boolean showEffective = android.preference.PreferenceManager
                .getDefaultSharedPreferences(requireContext())
                .getBoolean(com.example.ccrewards.ui.settings.SettingsFragment.PREF_BEST_CARD_SHOW_EFFECTIVE, true);
        adapter.setShowEffective(showEffective);
    }

    private void refreshTiles(Map<RewardCategory, List<BestCardForCategory>> ranked) {
        List<BestCardFragment.CategoryTileAdapter.TileData> tiles = new ArrayList<>();
        for (int i = 0; i < TRAVEL_SUBCATEGORIES.length; i++) {
            RewardCategory cat = TRAVEL_SUBCATEGORIES[i];
            BestCardForCategory top = BestCardViewModel.getTop(ranked, cat);
            tiles.add(new BestCardFragment.CategoryTileAdapter.TileData(cat, TRAVEL_SUBCATEGORY_LABELS[i], top));
        }
        adapter.setTiles(tiles);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
