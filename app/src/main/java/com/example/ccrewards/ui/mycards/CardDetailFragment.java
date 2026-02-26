package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.CardBenefit;
import com.example.ccrewards.data.model.ProductChangeRecord;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.databinding.FragmentCardDetailBinding;
import com.example.ccrewards.databinding.ItemBenefitDetailBinding;
import com.example.ccrewards.databinding.ItemHistoryRecordBinding;
import com.example.ccrewards.databinding.ItemRewardRateRowBinding;
import com.example.ccrewards.util.CurrencyUtil;
import com.example.ccrewards.util.DateUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CardDetailFragment extends Fragment {

    private FragmentCardDetailBinding binding;
    private CardDetailViewModel viewModel;
    private long userCardId;

    // Simple adapters using ViewBinding
    private SimpleRateAdapter rateAdapter;
    private SimpleBenefitAdapter benefitAdapter;
    private SimpleHistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCardDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CardDetailViewModel.class);

        // Get argument
        userCardId = getArguments() != null ? getArguments().getLong("userCardId", -1) : -1;
        viewModel.loadCard(userCardId);

        // Toolbar back navigation
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        // Edit menu item
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit_card) {
                showEditDialog();
                return true;
            }
            return false;
        });

        // RecyclerViews
        rateAdapter = new SimpleRateAdapter();
        binding.recyclerRewardRates.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerRewardRates.setAdapter(rateAdapter);
        binding.recyclerRewardRates.setNestedScrollingEnabled(false);

        benefitAdapter = new SimpleBenefitAdapter();
        binding.recyclerBenefits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBenefits.setAdapter(benefitAdapter);
        binding.recyclerBenefits.setNestedScrollingEnabled(false);

        historyAdapter = new SimpleHistoryAdapter();
        binding.recyclerHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHistory.setAdapter(historyAdapter);
        binding.recyclerHistory.setNestedScrollingEnabled(false);

        // Observe card details
        viewModel.getCardDetails().observe(getViewLifecycleOwner(), this::bindCardDetails);

        // Observe history
        viewModel.getHistory().observe(getViewLifecycleOwner(), records ->
                historyAdapter.setData(records));

        // Action buttons
        binding.btnEditRates.setOnClickListener(v -> {
            UserCardWithDetails current = viewModel.getCardDetails().getValue();
            if (current == null) return;
            Bundle args = new Bundle();
            args.putLong("userCardId", userCardId);
            args.putString("cardDefinitionId", current.definition.id);
            Navigation.findNavController(v).navigate(R.id.action_cardDetail_to_editRates, args);
        });

        binding.btnProductChange.setOnClickListener(v -> {
            UserCardWithDetails current = viewModel.getCardDetails().getValue();
            if (current == null) return;
            Bundle args = new Bundle();
            args.putLong("userCardId", userCardId);
            args.putString("currentCardDefinitionId", current.definition.id);
            args.putString("issuer", current.definition.issuer);
            Navigation.findNavController(v).navigate(R.id.action_cardDetail_to_productChange, args);
        });

        binding.btnAddBenefit.setOnClickListener(v -> {
            UserCardWithDetails current = viewModel.getCardDetails().getValue();
            if (current == null) return;
            Bundle args = new Bundle();
            args.putString("cardDefinitionId", current.definition.id);
            args.putLong("benefitId", -1L);
            Navigation.findNavController(v).navigate(R.id.action_cardDetail_to_addEditBenefit, args);
        });

        binding.btnDeleteCard.setOnClickListener(v -> confirmDelete());
    }

    private void bindCardDetails(UserCardWithDetails item) {
        if (item == null || item.definition == null) return;

        binding.toolbar.setTitle(item.definition.displayName);
        binding.detailColorStrip.setBackgroundColor((int) item.definition.cardColorPrimary);
        binding.tvDetailCardName.setText(item.definition.displayName);
        binding.tvDetailIssuer.setText(item.definition.issuer + " · " + item.definition.network);
        binding.tvDetailAnnualFee.setText(CurrencyUtil.formatAnnualFee(item.definition.annualFee));

        if (item.userCard.creditLimit > 0) {
            binding.tvDetailCreditLimit.setText(CurrencyUtil.centsToString(item.userCard.creditLimit * 100));
        } else {
            binding.tvDetailCreditLimit.setText("—");
        }

        binding.tvDetailOpenDate.setText(DateUtil.toDisplayString(item.userCard.openDate));

        if (item.userCard.nickname != null && !item.userCard.nickname.isEmpty()) {
            binding.tvDetailNickname.setVisibility(View.VISIBLE);
            binding.tvDetailNickname.setText("\u201C" + item.userCard.nickname + "\u201D");
        } else {
            binding.tvDetailNickname.setVisibility(View.GONE);
        }

        // Rates
        Map<String, String> rateDisplay = CardDetailViewModel.buildRateDisplay(item.rewardRates);
        rateAdapter.setData(rateDisplay);

        // Benefits
        benefitAdapter.setData(item.benefits);
    }

    private void showEditDialog() {
        UserCardWithDetails current = viewModel.getCardDetails().getValue();
        if (current == null) return;

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_card_details, null);
        TextInputEditText etNickname = dialogView.findViewById(R.id.et_dialog_nickname);
        TextInputEditText etCreditLimit = dialogView.findViewById(R.id.et_dialog_credit_limit);
        TextInputEditText etOpenDate = dialogView.findViewById(R.id.et_dialog_open_date);

        if (current.userCard.nickname != null) etNickname.setText(current.userCard.nickname);
        if (current.userCard.creditLimit > 0)
            etCreditLimit.setText(String.valueOf(current.userCard.creditLimit));

        // Hide open date for edit (it's preserved for account history)
        etOpenDate.setVisibility(View.GONE);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Card")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String nickname = etNickname.getText() != null
                            ? etNickname.getText().toString().trim() : "";
                    String limitStr = etCreditLimit.getText() != null
                            ? etCreditLimit.getText().toString().trim() : "";
                    int creditLimit = limitStr.isEmpty() ? 0 : Integer.parseInt(limitStr);

                    UserCard updated = current.userCard;
                    updated.nickname = nickname.isEmpty() ? null : nickname;
                    updated.creditLimit = creditLimit;
                    viewModel.updateCard(updated);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete() {
        UserCardWithDetails current = viewModel.getCardDetails().getValue();
        if (current == null) return;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Card")
                .setMessage("Remove " + current.definition.displayName + " from My Cards?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteCard(current.userCard);
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Simple inner adapters ────────────────────────────────────────────────

    private class SimpleRateAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<SimpleRateAdapter.VH> {
        private final List<Map.Entry<String, String>> entries = new ArrayList<>();

        void setData(Map<String, String> map) {
            entries.clear();
            if (map != null) entries.addAll(map.entrySet());
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemRewardRateRowBinding b = ItemRewardRateRowBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Map.Entry<String, String> entry = entries.get(position);
            holder.binding.tvRateCategory.setText(entry.getKey());
            holder.binding.tvRateValue.setText(entry.getValue());
            holder.binding.ivCustomized.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() { return entries.size(); }

        class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            final ItemRewardRateRowBinding binding;
            VH(ItemRewardRateRowBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }

    private class SimpleBenefitAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<SimpleBenefitAdapter.VH> {
        private List<CardBenefit> items = new ArrayList<>();

        void setData(List<CardBenefit> data) {
            items = data != null ? data : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemBenefitDetailBinding b = ItemBenefitDetailBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CardBenefit benefit = items.get(position);
            holder.binding.tvBenefitName.setText(benefit.name);
            holder.binding.tvBenefitAmount.setText(CurrencyUtil.centsToString(benefit.amountCents) + "/yr");
            holder.binding.chipBenefitPeriod.setText(formatPeriod(benefit.resetPeriod));
        }

        private String formatPeriod(com.example.ccrewards.data.model.ResetPeriod period) {
            switch (period) {
                case MONTHLY: return "Monthly";
                case QUARTERLY: return "Quarterly";
                case SEMI_ANNUALLY: return "Semi-annual";
                default: return "Annual";
            }
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            final ItemBenefitDetailBinding binding;
            VH(ItemBenefitDetailBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }

    private class SimpleHistoryAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<SimpleHistoryAdapter.VH> {
        private List<ProductChangeRecord> items = new ArrayList<>();

        void setData(List<ProductChangeRecord> data) {
            items = data != null ? data : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemHistoryRecordBinding b = ItemHistoryRecordBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ProductChangeRecord record = items.get(position);
            holder.binding.tvHistoryDate.setText(DateUtil.toDisplayString(record.changeDate));
            holder.binding.tvHistoryDescription.setText(
                    record.fromCardDefinitionId + " \u2192 " + record.toCardDefinitionId);
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            final ItemHistoryRecordBinding binding;
            VH(ItemHistoryRecordBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}
