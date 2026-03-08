package com.example.ccrewards.ui.mycards;

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

import com.example.ccrewards.R;
import com.example.ccrewards.databinding.FragmentMyCardsBinding;
import com.example.ccrewards.ui.common.CardFilterState;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MyCardsFragment extends Fragment {

    private FragmentMyCardsBinding binding;
    private MyCardsViewModel viewModel;
    private UserCardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMyCardsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MyCardsViewModel.class);

        // RecyclerView setup
        adapter = new UserCardAdapter();
        binding.recyclerMyCards.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMyCards.setAdapter(adapter);

        // Card click → Card Detail
        adapter.setOnCardClickListener(item -> {
            Bundle args = new Bundle();
            args.putLong("userCardId", item.userCard.id);
            Navigation.findNavController(view)
                    .navigate(R.id.action_myCards_to_cardDetail, args);
        });

        // Observe filtered card list
        viewModel.getFilteredCards().observe(getViewLifecycleOwner(), cards -> {
            adapter.submitList(cards);
            boolean isEmpty = cards == null || cards.isEmpty();
            binding.emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            binding.recyclerMyCards.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        // Sort button
        binding.btnSort.setOnClickListener(v -> showSortDialog());

        // Filter button
        binding.btnFilter.setOnClickListener(v -> {
            MyCardsFilterBottomSheet sheet = MyCardsFilterBottomSheet.newInstance(
                    viewModel.getCurrentFilter());
            sheet.show(getChildFragmentManager(), MyCardsFilterBottomSheet.TAG);
        });

        // Receive filter result
        getChildFragmentManager().setFragmentResultListener(
                MyCardsFilterBottomSheet.RESULT_KEY, getViewLifecycleOwner(),
                (key, result) -> {
                    CardFilterState state = parseFilterResult(result, true);
                    viewModel.setFilter(state);
                    updateFilterBadge(state.countActiveFilters());
                });

        // FAB → Add Card
        binding.fabAddCard.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_myCards_to_addCard));
    }

    private CardFilterState parseFilterResult(Bundle result, boolean includeMyCardsFields) {
        CardFilterState state = new CardFilterState();
        state.cardType = CardFilterState.CardType.valueOf(
                result.getString("cardType", "ALL"));
        java.util.ArrayList<String> issuers = result.getStringArrayList("issuers");
        if (issuers != null) state.issuers = new java.util.HashSet<>(issuers);
        java.util.ArrayList<String> networks = result.getStringArrayList("networks");
        if (networks != null) state.networks = new java.util.HashSet<>(networks);
        if (includeMyCardsFields) {
            state.anniversaryMonth = CardFilterState.AnniversaryFilter.valueOf(
                    result.getString("anniversaryMonth", "ANY"));
            state.cardAge = CardFilterState.CardAgeFilter.valueOf(
                    result.getString("cardAge", "ANY"));
        }
        return state;
    }

    private void showSortDialog() {
        String[] options = {
                "Default (date added)",
                "Open date",
                "Alphabetical",
                "Annual fee",
                "Issuer",
                "Anniversary month"
        };
        MyCardsViewModel.SortOrder[] orders = {
                MyCardsViewModel.SortOrder.DEFAULT,
                MyCardsViewModel.SortOrder.OPEN_DATE,
                MyCardsViewModel.SortOrder.ALPHABETICAL,
                MyCardsViewModel.SortOrder.ANNUAL_FEE,
                MyCardsViewModel.SortOrder.ISSUER,
                MyCardsViewModel.SortOrder.ANNIVERSARY_MONTH
        };
        MyCardsViewModel.SortOrder current = viewModel.getCurrentSortOrder();
        int checkedItem = 0;
        for (int i = 0; i < orders.length; i++) {
            if (orders[i] == current) { checkedItem = i; break; }
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sort by")
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    viewModel.setSort(orders[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateFilterBadge(int activeCount) {
        if (activeCount > 0) {
            binding.tvFilterBadge.setText(String.valueOf(activeCount));
            binding.tvFilterBadge.setVisibility(View.VISIBLE);
        } else {
            binding.tvFilterBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
