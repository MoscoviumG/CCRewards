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

        // Filter chip group
        binding.filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_all) {
                viewModel.setFilter(MyCardsViewModel.Filter.ALL);
            } else if (id == R.id.chip_personal) {
                viewModel.setFilter(MyCardsViewModel.Filter.PERSONAL);
            } else if (id == R.id.chip_business) {
                viewModel.setFilter(MyCardsViewModel.Filter.BUSINESS);
            }
        });

        // FAB → Add Card
        binding.fabAddCard.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_myCards_to_addCard));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
