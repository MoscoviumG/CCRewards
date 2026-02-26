package com.example.ccrewards.ui.credits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.databinding.FragmentCreditsBinding;
import com.example.ccrewards.databinding.ItemBenefitRowBinding;
import com.example.ccrewards.databinding.ItemCreditsHeaderBinding;
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

        adapter = new CreditsListAdapter(viewModel);
        binding.recyclerCredits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCredits.setAdapter(adapter);

        viewModel.getDisplayItems().observe(getViewLifecycleOwner(), items -> {
            boolean empty = items == null || items.isEmpty();
            binding.creditsEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.recyclerCredits.setVisibility(empty ? View.GONE : View.VISIBLE);
            adapter.setItems(items);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Credits Adapter ──────────────────────────────────────────────────────

    private static class CreditsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<CreditsViewModel.ListItem> items = new ArrayList<>();
        private final CreditsViewModel viewModel;

        CreditsListAdapter(CreditsViewModel vm) {
            this.viewModel = vm;
        }

        void setItems(List<CreditsViewModel.ListItem> data) {
            items = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).type;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == CreditsViewModel.ListItem.TYPE_HEADER) {
                ItemCreditsHeaderBinding b = ItemCreditsHeaderBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
                return new HeaderVH(b);
            }
            ItemBenefitRowBinding b = ItemBenefitRowBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new BenefitVH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CreditsViewModel.ListItem item = items.get(position);
            if (holder instanceof HeaderVH) {
                HeaderVH h = (HeaderVH) holder;
                h.binding.tvCreditsSectionTitle.setText(item.headerLabel);
                h.binding.tvCreditsDaysReset.setText(item.daysUntilReset + " days to reset");
            } else if (holder instanceof BenefitVH && item.benefitWithUsage != null) {
                BenefitVH b = (BenefitVH) holder;
                b.bind(item.benefitWithUsage, viewModel);
            }
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class HeaderVH extends RecyclerView.ViewHolder {
            final ItemCreditsHeaderBinding binding;
            HeaderVH(ItemCreditsHeaderBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }

        static class BenefitVH extends RecyclerView.ViewHolder {
            final ItemBenefitRowBinding binding;
            BenefitVH(ItemBenefitRowBinding b) {
                super(b.getRoot());
                binding = b;
            }

            void bind(com.example.ccrewards.data.model.relations.BenefitWithUsage bwu,
                      CreditsViewModel vm) {
                binding.tvBenefitCardName.setText(bwu.definition.displayName +
                        (bwu.userCard.nickname != null && !bwu.userCard.nickname.isEmpty()
                                ? " (\u201C" + bwu.userCard.nickname + "\u201D)" : ""));
                binding.tvBenefitRowName.setText(bwu.benefit.name);
                binding.tvBenefitRowAmount.setText(CurrencyUtil.centsToString(bwu.benefit.amountCents)
                        + " / " + formatPeriod(bwu.benefit.resetPeriod));

                // Suppress listener while setting state
                binding.switchBenefitUsed.setOnCheckedChangeListener(null);
                binding.switchBenefitUsed.setChecked(bwu.isUsed());
                binding.switchBenefitUsed.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    vm.markUsed(bwu.userCard.id, bwu.benefit.id, bwu.benefit.resetPeriod, isChecked);
                });
            }

            private String formatPeriod(com.example.ccrewards.data.model.ResetPeriod p) {
                switch (p) {
                    case MONTHLY: return "mo";
                    case QUARTERLY: return "qtr";
                    case SEMI_ANNUALLY: return "6mo";
                    default: return "yr";
                }
            }
        }
    }
}
