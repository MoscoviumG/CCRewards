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

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.databinding.FragmentAddCardBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddCardFragment extends Fragment {

    private FragmentAddCardBinding binding;
    private AddCardViewModel viewModel;
    private CatalogAdapter catalogAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddCardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddCardViewModel.class);

        // Toolbar back navigation
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        // RecyclerView
        catalogAdapter = new CatalogAdapter();
        binding.recyclerCatalog.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCatalog.setAdapter(catalogAdapter);

        // Card click → navigate to full-screen detail page
        catalogAdapter.setOnItemClickListener(new CatalogAdapter.OnItemClickListener() {
            @Override
            public void onCardClick(CardDefinition card) {
                Bundle args = new Bundle();
                args.putString("cardDefinitionId", card.id);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_addCard_to_addCardDetail, args);
            }

            @Override
            public void onCreateCustomClick() {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_addCard_to_addCustomCard);
            }
        });

        // Observe filtered catalog
        viewModel.getDisplayedCards().observe(getViewLifecycleOwner(), cards ->
                catalogAdapter.submitList(cards));

        // Search
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter button
        binding.btnFilterAdd.setOnClickListener(v -> {
            AddCardFilterBottomSheet sheet = AddCardFilterBottomSheet.newInstance(
                    viewModel.getCurrentFilter());
            sheet.show(getChildFragmentManager(), AddCardFilterBottomSheet.TAG);
        });

        // Receive filter result
        getChildFragmentManager().setFragmentResultListener(
                AddCardFilterBottomSheet.RESULT_KEY, getViewLifecycleOwner(),
                (key, result) -> {
                    com.example.ccrewards.ui.common.CardFilterState state =
                            new com.example.ccrewards.ui.common.CardFilterState();
                    state.cardType = com.example.ccrewards.ui.common.CardFilterState.CardType.valueOf(
                            result.getString("cardType", "ALL"));
                    java.util.ArrayList<String> issuers = result.getStringArrayList("issuers");
                    if (issuers != null) state.issuers = new java.util.HashSet<>(issuers);
                    java.util.ArrayList<String> networks = result.getStringArrayList("networks");
                    if (networks != null) state.networks = new java.util.HashSet<>(networks);
                    viewModel.setFilter(state);
                    int count = state.countActiveFilters();
                    if (count > 0) {
                        binding.tvFilterBadgeAdd.setText(String.valueOf(count));
                        binding.tvFilterBadgeAdd.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvFilterBadgeAdd.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
