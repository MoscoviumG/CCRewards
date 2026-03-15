package com.example.ccrewards.ui.credits;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.data.model.FreeNightAward;
import com.example.ccrewards.data.model.FreeNightValuation;
import com.example.ccrewards.data.model.UserCard;
import com.example.ccrewards.data.repository.CardRepository;
import com.example.ccrewards.data.repository.FreeNightRepository;
import com.example.ccrewards.databinding.FragmentCreditsBinding;
import com.example.ccrewards.databinding.ItemBenefitRowBinding;
import com.example.ccrewards.databinding.ItemFreeNightRowBinding;
import com.example.ccrewards.util.CurrencyUtil;
import com.example.ccrewards.util.DateUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CreditsFragment extends Fragment {

    @Inject FreeNightRepository freeNightRepository;
    @Inject CardRepository cardRepository;

    private static final String PREFS_NAME = "credits_prefs";
    private static final String KEY_FN_HIDE_USED = "fn_hide_used";

    private FragmentCreditsBinding binding;
    private CreditsViewModel viewModel;
    private CreditsListAdapter adapter;
    private FreeNightAdapter fnAdapter;
    private final MutableLiveData<List<FreeNightItem>> fnItems = new MutableLiveData<>();
    private boolean fnHideUsed = false;

    // ── Free Night display model ──────────────────────────────────────────────

    public static class FreeNightItem {
        public final FreeNightAward award;
        public final String cardDisplayName;
        public final String typeLabel;

        FreeNightItem(FreeNightAward award, String cardDisplayName, String typeLabel) {
            this.award = award;
            this.cardDisplayName = cardDisplayName;
            this.typeLabel = typeLabel;
        }
    }

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

        // ── Benefits adapter ─────────────────────────────────────────────────
        adapter = new CreditsListAdapter(
                item -> {
                    Bundle args = new Bundle();
                    args.putLong("userCardId", item.benefitWithUsage.userCard.id);
                    args.putLong("benefitId",  item.benefitWithUsage.benefit.id);
                    Navigation.findNavController(requireView())
                            .navigate(com.example.ccrewards.R.id.action_credits_to_benefitDetail, args);
                },
                item -> viewModel.toggleStar(
                        item.benefitWithUsage.userCard.id,
                        item.benefitWithUsage.benefit.id)
        );
        binding.recyclerCredits.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerCredits.setAdapter(adapter);

        viewModel.getDisplayItems().observe(getViewLifecycleOwner(), items -> {
            if (binding.tabCredits.getSelectedTabPosition() != 0) return;
            boolean empty = items == null || items.isEmpty();
            binding.creditsEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.recyclerCredits.setVisibility(empty ? View.GONE : View.VISIBLE);
            adapter.setItems(items);
        });

        binding.switchHideUsed.setChecked(viewModel.getInitialHideUsed());
        binding.switchHideUsed.setOnCheckedChangeListener((btn, checked) ->
                viewModel.setHideUsed(checked));

        // Free Nights hide-used toggle
        SharedPreferences prefs = requireContext()
                .getSharedPreferences(PREFS_NAME, android.content.Context.MODE_PRIVATE);
        fnHideUsed = prefs.getBoolean(KEY_FN_HIDE_USED, false);
        binding.switchFnHideUsed.setChecked(fnHideUsed);
        binding.switchFnHideUsed.setOnCheckedChangeListener((btn, checked) -> {
            fnHideUsed = checked;
            prefs.edit().putBoolean(KEY_FN_HIDE_USED, checked).apply();
            if (binding.tabCredits.getSelectedTabPosition() == 1) loadFreeNights();
        });

        if (binding.etCreditsSearch != null) {
            binding.etCreditsSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                    viewModel.setSearchQuery(s != null ? s.toString() : "");
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // ── Free Nights adapter ───────────────────────────────────────────────
        fnAdapter = new FreeNightAdapter();
        binding.recyclerFreeNights.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerFreeNights.setAdapter(fnAdapter);

        fnItems.observe(getViewLifecycleOwner(), items -> {
            if (binding.tabCredits.getSelectedTabPosition() != 1) return;
            boolean empty = items == null || items.isEmpty();
            binding.fnEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            binding.recyclerFreeNights.setVisibility(empty ? View.GONE : View.VISIBLE);
            fnAdapter.setItems(items);
        });

        // ── Tab switching ─────────────────────────────────────────────────────
        binding.tabCredits.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showBenefitsTab();
                } else {
                    showFreeNightsTab();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showBenefitsTab() {
        binding.layoutBenefitsControls.setVisibility(View.VISIBLE);
        binding.layoutFnControls.setVisibility(View.GONE);
        binding.recyclerFreeNights.setVisibility(View.GONE);
        binding.fnEmptyState.setVisibility(View.GONE);
        List<CreditsViewModel.ListItem> items = viewModel.getDisplayItems().getValue();
        boolean empty = items == null || items.isEmpty();
        binding.creditsEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerCredits.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private void showFreeNightsTab() {
        binding.layoutBenefitsControls.setVisibility(View.GONE);
        binding.layoutFnControls.setVisibility(View.VISIBLE);
        binding.creditsEmptyState.setVisibility(View.GONE);
        binding.recyclerCredits.setVisibility(View.GONE);
        loadFreeNights();
    }

    private void loadFreeNights() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<UserCard> cards = cardRepository.getOpenUserCardsSync();
            if (cards == null || cards.isEmpty()) {
                fnItems.postValue(new ArrayList<>());
                return;
            }

            List<Long> userCardIds = new ArrayList<>();
            for (UserCard c : cards) userCardIds.add(c.id);

            // Build a map from userCardId -> UserCard for display name lookup
            Map<Long, UserCard> cardMap = new HashMap<>();
            for (UserCard c : cards) cardMap.put(c.id, c);

            // Build a map from cardDefinitionId -> CardDefinition for display names
            Map<String, CardDefinition> defMap = new HashMap<>();
            for (UserCard c : cards) {
                if (!defMap.containsKey(c.cardDefinitionId)) {
                    CardDefinition def = cardRepository.getCardDefinitionSync(c.cardDefinitionId);
                    if (def != null) defMap.put(c.cardDefinitionId, def);
                }
            }

            // Migrate any legacy multi-count awards to individual records
            freeNightRepository.splitMultiCountAwardsSync(userCardIds);

            // Refresh recurring awards
            freeNightRepository.refreshRecurringAwardsSync(userCardIds);

            List<FreeNightAward> awards = freeNightRepository.getAllAwardsForCardsSync(userCardIds);

            // Count WB awards per (userCardId + typeKey) to number duplicates
            Map<String, Integer> wbCountByKey = new HashMap<>();
            for (FreeNightAward award : awards) {
                if (award.isFromWelcomeBonus) {
                    String key = award.userCardId + ":" + award.typeKey;
                    wbCountByKey.merge(key, 1, Integer::sum);
                }
            }
            Map<String, Integer> wbIndexByKey = new HashMap<>();

            List<FreeNightItem> result = new ArrayList<>();
            for (FreeNightAward award : awards) {
                UserCard c = cardMap.get(award.userCardId);
                if (c == null) continue;
                CardDefinition def = defMap.get(c.cardDefinitionId);
                String displayName = def != null ? def.displayName : c.cardDefinitionId;
                String cardName = UserCard.label(displayName, c.lastFour, c.nickname);
                FreeNightValuation val = freeNightRepository.getValuationSync(award.typeKey);
                String baseLabel = award.label != null ? award.label
                        : (val != null ? val.label : award.typeKey);
                String typeLabel;
                if (award.isFromWelcomeBonus) {
                    String key = award.userCardId + ":" + award.typeKey;
                    if (wbCountByKey.getOrDefault(key, 1) > 1) {
                        int idx = wbIndexByKey.getOrDefault(key, 0) + 1;
                        wbIndexByKey.put(key, idx);
                        typeLabel = baseLabel + " · FN " + idx;
                    } else {
                        typeLabel = baseLabel;
                    }
                } else {
                    typeLabel = baseLabel;
                }
                result.add(new FreeNightItem(award, cardName, typeLabel));
            }

            // Sort: unused first, then by expiration date ascending (nulls last)
            result.sort((a, b) -> {
                boolean aUsed = a.award.usedCount >= a.award.totalCount;
                boolean bUsed = b.award.usedCount >= b.award.totalCount;
                if (aUsed != bUsed) return aUsed ? 1 : -1;
                if (a.award.expirationDate == null && b.award.expirationDate == null) return 0;
                if (a.award.expirationDate == null) return 1;
                if (b.award.expirationDate == null) return -1;
                return a.award.expirationDate.compareTo(b.award.expirationDate);
            });

            // Apply hide-used filter
            if (fnHideUsed) {
                result.removeIf(item -> item.award.usedCount >= item.award.totalCount);
            }

            fnItems.postValue(result);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) viewModel.refresh();
        if (binding != null && binding.tabCredits.getSelectedTabPosition() == 1) {
            loadFreeNights();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Benefits adapter ──────────────────────────────────────────────────────

    interface OnItemClickListener {
        void onClick(CreditsViewModel.ListItem item);
    }

    private static class CreditsListAdapter
            extends RecyclerView.Adapter<CreditsListAdapter.BenefitVH> {

        private List<CreditsViewModel.ListItem> items = new ArrayList<>();
        private final OnItemClickListener clickListener;
        private final OnItemClickListener starListener;

        CreditsListAdapter(OnItemClickListener clickListener, OnItemClickListener starListener) {
            this.clickListener = clickListener;
            this.starListener = starListener;
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
            holder.bind(items.get(position), clickListener, starListener);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class BenefitVH extends RecyclerView.ViewHolder {
            final ItemBenefitRowBinding binding;

            BenefitVH(ItemBenefitRowBinding b) {
                super(b.getRoot());
                binding = b;
            }

            void bind(CreditsViewModel.ListItem item, OnItemClickListener listener,
                      OnItemClickListener starListener) {
                com.example.ccrewards.data.model.relations.BenefitWithUsage bwu =
                        item.benefitWithUsage;

                binding.tvBenefitCardName.setText(
                        UserCard.label(bwu.definition.displayName, bwu.userCard.lastFour, bwu.userCard.nickname));
                binding.tvBenefitRowName.setText(bwu.benefit.name);

                String daysLabel;
                if (bwu.benefit.isOneTime) {
                    daysLabel = item.daysUntilReset != Integer.MAX_VALUE
                            ? item.daysUntilReset + " days \u00B7 One-time"
                            : "One-time";
                } else if (item.isAnniversary) {
                    daysLabel = item.daysUntilReset + " days \u00B7 Anniversary";
                } else {
                    daysLabel = item.daysUntilReset + " days";
                }
                binding.tvDaysReset.setText(daysLabel);

                binding.ivStar.setImageResource(item.isStarred
                        ? com.example.ccrewards.R.drawable.ic_star
                        : com.example.ccrewards.R.drawable.ic_star_outline);
                binding.ivStar.setOnClickListener(v -> starListener.onClick(item));

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

    // ── Free Nights adapter ────────────────────────────────────────────────────

    private class FreeNightAdapter extends RecyclerView.Adapter<FreeNightAdapter.VH> {
        private List<FreeNightItem> items = new ArrayList<>();

        void setItems(List<FreeNightItem> data) {
            items = data != null ? new ArrayList<>(data) : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemFreeNightRowBinding b = ItemFreeNightRowBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            FreeNightItem item = items.get(position);
            FreeNightAward award = item.award;

            holder.binding.tvFnCardName.setText(item.cardDisplayName);
            holder.binding.tvFnLabel.setText(item.typeLabel);

            // Expiry
            if (award.expirationDate != null) {
                holder.binding.tvFnExpiry.setText("Exp " + DateUtil.toDisplayString(award.expirationDate));
            } else {
                holder.binding.tvFnExpiry.setText("");
            }

            // Status
            boolean isUsed = award.usedCount >= award.totalCount;
            String statusText;
            if (isUsed) {
                statusText = "Used";
            } else if (award.isRecurring && award.renewalMonth != null && award.renewalDay != null) {
                statusText = String.format("Unused  \u00B7  Renews %d/%d",
                        award.renewalMonth, award.renewalDay);
            } else if (award.isFromWelcomeBonus) {
                statusText = "Unused  \u00B7  From Welcome Bonus";
            } else {
                statusText = "Unused";
            }
            holder.binding.tvFnStatus.setText(statusText);

            // Toggle button
            holder.binding.btnFnToggle.setText(isUsed ? "Unmark" : "Mark Used");
            holder.binding.btnFnToggle.setOnClickListener(v ->
                    freeNightRepository.toggleUsed(award.id, () -> loadFreeNights()));
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            final ItemFreeNightRowBinding binding;

            VH(ItemFreeNightRowBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }
}
