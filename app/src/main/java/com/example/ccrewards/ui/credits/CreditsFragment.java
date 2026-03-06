package com.example.ccrewards.ui.credits;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.databinding.FragmentCreditsBinding;
import com.example.ccrewards.databinding.ItemBenefitRowBinding;
import com.example.ccrewards.util.CurrencyUtil;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreditsFragment extends Fragment {

    private FragmentCreditsBinding binding;
    private CreditsViewModel viewModel;
    private CreditsListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCreditsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreditsViewModel.class);

        adapter = new CreditsListAdapter(item -> {
            Bundle args = new Bundle();
            args.putLong("userCardId", item.benefitWithUsage.userCard.id);
            args.putLong("benefitId",  item.benefitWithUsage.benefit.id);
            Navigation.findNavController(requireView())
                    .navigate(com.example.ccrewards.R.id.action_credits_to_benefitDetail, args);
        });

        binding.recyclerCredits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCredits.setAdapter(adapter);

        viewModel.getDisplayItems().observe(getViewLifecycleOwner(), items -> {
            boolean empty = items == null || items.isEmpty();
            binding.creditsEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.recyclerCredits.setVisibility(empty ? View.GONE : View.VISIBLE);
            adapter.setItems(items);
        });

        binding.switchHideUsed.setOnCheckedChangeListener((btn, checked) ->
                viewModel.setHideUsed(checked));

        if (binding.etCreditsSearch != null) {
            binding.etCreditsSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                    viewModel.setSearchQuery(s != null ? s.toString() : "");
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh usage data when returning from BenefitDetailFragment
        if (viewModel != null) viewModel.refresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Adapter ──────────────────────────────────────────────────────────────

    interface OnItemClickListener {
        void onClick(CreditsViewModel.ListItem item);
    }

    private static class CreditsListAdapter
            extends RecyclerView.Adapter<CreditsListAdapter.BenefitVH> {

        private List<CreditsViewModel.ListItem> items = new ArrayList<>();
        private final OnItemClickListener clickListener;

        CreditsListAdapter(OnItemClickListener listener) {
            this.clickListener = listener;
        }

        void setItems(List<CreditsViewModel.ListItem> data) {
            items = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public BenefitVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemBenefitRowBinding b = ItemBenefitRowBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new BenefitVH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull BenefitVH holder, int position) {
            holder.bind(items.get(position), clickListener);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class BenefitVH extends RecyclerView.ViewHolder {
            final ItemBenefitRowBinding binding;

            BenefitVH(ItemBenefitRowBinding b) {
                super(b.getRoot());
                binding = b;
            }

            void bind(CreditsViewModel.ListItem item, OnItemClickListener listener) {
                com.example.ccrewards.data.model.relations.BenefitWithUsage bwu =
                        item.benefitWithUsage;

                binding.tvBenefitCardName.setText(
                        UserCard.label(bwu.definition.displayName, bwu.userCard.lastFour, bwu.userCard.nickname));
                binding.tvBenefitRowName.setText(bwu.benefit.name);

                // Days to reset
                String daysLabel = item.isAnniversary
                        ? item.daysUntilReset + " days \u00B7 Anniversary"
                        : item.daysUntilReset + " days";
                binding.tvDaysReset.setText(daysLabel);

                // Used amount + progress
                if (bwu.benefit.amountCents > 0) {
                    int usedCents = item.usedCents;
                    int totalCents = bwu.benefit.amountCents;
                    binding.tvBenefitUsedAmount.setText(
                            "$" + (usedCents / 100) + " of "
                                    + CurrencyUtil.centsToString(totalCents) + " used");
                    int progress = totalCents > 0 ? (usedCents * 100 / totalCents) : 0;
                    binding.progressBenefit.setProgressCompat(progress, false);
                    binding.progressBenefit.setVisibility(View.VISIBLE);
                } else {
                    binding.tvBenefitUsedAmount.setText(bwu.isUsed() ? "Used" : "Not used");
                    binding.progressBenefit.setVisibility(View.GONE);
                }

                binding.getRoot().setOnClickListener(v -> listener.onClick(item));
            }
        }
    }
}
