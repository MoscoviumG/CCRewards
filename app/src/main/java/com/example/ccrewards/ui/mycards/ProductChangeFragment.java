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
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.databinding.FragmentProductChangeBinding;
import com.example.ccrewards.databinding.ItemProductChangeTargetBinding;
import com.example.ccrewards.util.CurrencyUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductChangeFragment extends Fragment {

    private FragmentProductChangeBinding binding;
    private ProductChangeViewModel viewModel;
    private long userCardId;
    private String currentCardDefinitionId;
    private String issuer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProductChangeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProductChangeViewModel.class);

        userCardId = getArguments() != null ? getArguments().getLong("userCardId", -1) : -1;
        currentCardDefinitionId = getArguments() != null
                ? getArguments().getString("currentCardDefinitionId") : null;
        issuer = getArguments() != null ? getArguments().getString("issuer") : null;

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        if (issuer != null) {
            binding.tvPcIssuerLabel.setText(issuer + " cards");
        }

        PCTargetAdapter adapter = new PCTargetAdapter(viewModel);
        binding.recyclerPcTargets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerPcTargets.setAdapter(adapter);

        adapter.setOnClickListener(card -> confirmProductChange(card));

        if (currentCardDefinitionId != null && issuer != null) {
            viewModel.load(currentCardDefinitionId, issuer);
        }

        viewModel.getTargets().observe(getViewLifecycleOwner(), adapter::setCards);
    }

    private void confirmProductChange(CardDefinition target) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Product Change")
                .setMessage("Change to " + target.displayName + "?\n\n"
                        + "Your account history and open date will be preserved.")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    viewModel.performProductChange(userCardId, target.id, null);
                    // Navigate back to My Cards root
                    Navigation.findNavController(requireView()).popBackStack(
                            com.example.ccrewards.R.id.myCardsFragment, false);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── PC Target Adapter ────────────────────────────────────────────────────

    private static class PCTargetAdapter extends RecyclerView.Adapter<PCTargetAdapter.VH> {

        interface OnClickListener {
            void onClick(CardDefinition card);
        }

        private List<CardDefinition> cards = new ArrayList<>();
        private OnClickListener listener;
        private final ProductChangeViewModel viewModel;

        PCTargetAdapter(ProductChangeViewModel vm) {
            this.viewModel = vm;
        }

        void setCards(List<CardDefinition> data) {
            cards = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        void setOnClickListener(OnClickListener l) {
            listener = l;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemProductChangeTargetBinding b = ItemProductChangeTargetBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CardDefinition card = cards.get(position);
            holder.binding.pcColorStrip.setBackgroundColor((int) card.cardColorPrimary);
            holder.binding.tvPcCardName.setText(card.displayName);
            holder.binding.tvPcFee.setText(CurrencyUtil.formatAnnualFee(card.annualFee));
            boolean suggested = viewModel.isSuggested(card.id);
            holder.binding.chipPcSuggested.setVisibility(suggested ? View.VISIBLE : View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClick(card);
            });
        }

        @Override
        public int getItemCount() { return cards.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final ItemProductChangeTargetBinding binding;
            VH(ItemProductChangeTargetBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}
