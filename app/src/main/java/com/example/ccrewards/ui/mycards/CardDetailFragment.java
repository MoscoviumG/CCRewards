package com.example.ccrewards.ui.mycards;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;

import androidx.appcompat.app.AlertDialog;

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
import com.example.ccrewards.data.model.WelcomeBonus;
import com.example.ccrewards.data.model.relations.UserCardWithDetails;
import com.example.ccrewards.databinding.FragmentCardDetailBinding;
import com.example.ccrewards.databinding.ItemBenefitDetailBinding;
import com.example.ccrewards.databinding.ItemHistoryRecordBinding;
import com.example.ccrewards.databinding.ItemRewardRateRowBinding;
import com.example.ccrewards.databinding.ItemRotationalBonusBannerBinding;
import com.example.ccrewards.util.CurrencyUtil;
import com.example.ccrewards.util.DateUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.SeekBar;

import android.app.DatePickerDialog;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CardDetailFragment extends Fragment {

    private static final int[] PRESET_COLORS = {
        0xFF1A237E, 0xFF0D47A1, 0xFF1565C0, 0xFF006994, 0xFF00695C,
        0xFF1B5E20, 0xFF33691E, 0xFF880E4F, 0xFFB71C1C, 0xFF6A1520,
        0xFF4A148C, 0xFF7B1FA2, 0xFFAD1457, 0xFFE65100, 0xFF212121,
        0xFF37474F, 0xFF455A64, 0xFF546E7A, 0xFFBF9000, 0xFF78909C,
    };

    private FragmentCardDetailBinding binding;
    private CardDetailViewModel viewModel;
    private long userCardId;
    private String currentCurrencyName = "";

    // Simple adapters using ViewBinding
    private SimpleRateAdapter rateAdapter;
    private SimpleBenefitAdapter benefitAdapter;
    private SimpleHistoryAdapter historyAdapter;
    private SimpleRotationalBonusAdapter rbAdapter;

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

        rbAdapter = new SimpleRotationalBonusAdapter();
        binding.recyclerQuarterlyBonuses.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerQuarterlyBonuses.setAdapter(rbAdapter);
        binding.recyclerQuarterlyBonuses.setNestedScrollingEnabled(false);

        // Observe card details
        viewModel.getCardDetails().observe(getViewLifecycleOwner(), this::bindCardDetails);

        // Observe history
        viewModel.getHistory().observe(getViewLifecycleOwner(), records ->
                historyAdapter.setData(records));

        // Observe quarterly bonuses
        viewModel.getRotationalBonuses().observe(getViewLifecycleOwner(), items ->
                rbAdapter.setData(items != null ? items : new ArrayList<>()));

        // Observe credit usage
        viewModel.getCreditUsageThisYear().observe(getViewLifecycleOwner(), cents -> {
            if (cents != null && cents > 0) {
                binding.tvDetailCreditUsed.setVisibility(View.VISIBLE);
                binding.tvDetailCreditUsed.setText("$" + (cents / 100) + " in credits used this year");
            } else {
                binding.tvDetailCreditUsed.setVisibility(View.GONE);
            }
        });

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

        binding.btnChangeColor.setOnClickListener(v -> showColorPickerDialog());

        binding.btnAddQuarterlyBenefit.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putLong("userCardId", userCardId);
            args.putString("currencyName", currentCurrencyName);
            Navigation.findNavController(v)
                    .navigate(R.id.action_cardDetail_to_addRotationalBonus, args);
        });

        // ── Welcome Bonus ────────────────────────────────────────────────────

        viewModel.getWelcomeBonus().observe(getViewLifecycleOwner(), this::bindWelcomeBonus);

        // Listen for result from the bottom sheet
        getChildFragmentManager().setFragmentResultListener(
                WelcomeBonusBottomSheet.RESULT_KEY, getViewLifecycleOwner(), (key, result) -> {
                    WelcomeBonus wb = new WelcomeBonus();
                    wb.userCardId = userCardId;
                    wb.bonusPoints = result.getInt("bonus_points");
                    wb.bonusCurrencyName = currentCurrencyName;
                    wb.spendRequirementCents = result.getInt("spend_req_cents");
                    long epoch = result.getLong("deadline_epoch", -1L);
                    wb.deadline = epoch == -1L ? null : LocalDate.ofEpochDay(epoch);
                    wb.showInBestCard = result.getBoolean("show_in_best_card", true);
                    wb.achieved = false;
                    viewModel.upsertWelcomeBonus(wb);
                });

        binding.btnAddWelcomeBonus.setOnClickListener(v ->
                WelcomeBonusBottomSheet.newInstance(currentCurrencyName)
                        .show(getChildFragmentManager(), WelcomeBonusBottomSheet.TAG));

        binding.btnWbEdit.setOnClickListener(v -> {
            WelcomeBonus current = viewModel.getWelcomeBonus().getValue();
            if (current != null)
                WelcomeBonusBottomSheet.newInstance(current)
                        .show(getChildFragmentManager(), WelcomeBonusBottomSheet.TAG);
        });

        binding.btnWbMarkAchieved.setOnClickListener(v ->
                viewModel.markWelcomeBonusAchieved(userCardId));

        binding.btnWbRemove.setOnClickListener(v -> {
            WelcomeBonus wb = viewModel.getWelcomeBonus().getValue();
            if (wb == null) return;
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Remove Welcome Bonus")
                    .setMessage("Remove the welcome bonus from this card?")
                    .setPositiveButton("Remove", (d, w) -> viewModel.deleteWelcomeBonus(wb))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void bindCardDetails(UserCardWithDetails item) {
        if (item == null || item.definition == null) return;

        currentCurrencyName = item.definition.rewardCurrencyName;
        String cardLabel = UserCard.label(item.definition.displayName, item.userCard.lastFour, item.userCard.nickname);
        binding.toolbar.setTitle(cardLabel);
        int stripColor = item.userCard.customColorPrimary != null
                ? item.userCard.customColorPrimary.intValue()
                : (int) item.definition.cardColorPrimary;
        binding.detailColorStrip.setBackgroundColor(stripColor);
        binding.tvDetailCardName.setText(cardLabel);
        binding.tvDetailIssuer.setText(item.definition.issuer + " · " + item.definition.network);
        binding.tvDetailAnnualFee.setText(CurrencyUtil.formatAnnualFee(item.definition.annualFee));

        if (item.userCard.creditLimit > 0) {
            binding.tvDetailCreditLimit.setText(CurrencyUtil.centsToString(item.userCard.creditLimit * 100));
        } else {
            binding.tvDetailCreditLimit.setText("—");
        }

        binding.tvDetailOpenDate.setText(DateUtil.toDisplayString(item.userCard.openDate));

        // Nickname is now included inline in the card label above
        binding.tvDetailNickname.setVisibility(View.GONE);

        // Rates
        Map<String, String> rateDisplay = CardDetailViewModel.buildRateDisplay(item.rewardRates);
        rateAdapter.setData(rateDisplay);

        // Show rotating-category banner if this card has quarterly rotating rates
        boolean hasRotating = false;
        if (item.rewardRates != null) {
            for (com.example.ccrewards.data.model.RewardRate r : item.rewardRates) {
                if (r.isChoiceCategory && r.choiceGroupId != null
                        && r.choiceGroupId.contains("rotating")) {
                    hasRotating = true;
                    break;
                }
            }
        }
        binding.cardRotatingInfo.setVisibility(hasRotating ? View.VISIBLE : View.GONE);

        // Benefits
        benefitAdapter.setData(item.benefits);
    }

    private void bindWelcomeBonus(WelcomeBonus wb) {
        if (wb == null) {
            binding.cardWelcomeBonus.setVisibility(View.GONE);
            binding.btnAddWelcomeBonus.setVisibility(View.VISIBLE);
            return;
        }
        binding.cardWelcomeBonus.setVisibility(View.VISIBLE);
        binding.btnAddWelcomeBonus.setVisibility(View.GONE);

        boolean cashBack = WelcomeBonusBottomSheet.isCashBack(wb.bonusCurrencyName);
        if (cashBack) {
            binding.tvWbBonus.setText(CurrencyUtil.centsToString(wb.bonusPoints) + " Cash Back");
        } else {
            binding.tvWbBonus.setText(NumberFormat.getInstance(Locale.US).format(wb.bonusPoints)
                    + " " + shortCurrency(wb.bonusCurrencyName));
        }

        binding.tvWbSpend.setText("Spend " + CurrencyUtil.centsToString(wb.spendRequirementCents));
        binding.tvWbDeadline.setText(wb.deadline != null
                ? "by " + DateUtil.toDisplayString(wb.deadline) : "No deadline");

        binding.btnWbMarkAchieved.setVisibility(wb.achieved ? View.GONE : View.VISIBLE);
        binding.tvWbAchievedLabel.setVisibility(wb.achieved ? View.VISIBLE : View.GONE);
    }

    private static String shortCurrency(String name) {
        if (name == null) return "pts";
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
            default:                               return "pts";
        }
    }

    private void showColorPickerDialog() {
        UserCardWithDetails current = viewModel.getCardDetails().getValue();
        if (current == null) return;

        float density = getResources().getDisplayMetrics().density;
        int swatchSize = (int) (52 * density);
        int swatchMargin = (int) (5 * density);
        int padding = (int) (16 * density);

        GridLayout grid = new GridLayout(requireContext());
        grid.setColumnCount(5);
        grid.setPadding(padding, padding, padding, 0);

        long effectiveColor = current.userCard.customColorPrimary != null
                ? current.userCard.customColorPrimary
                : current.definition.cardColorPrimary;

        AlertDialog[] dialogHolder = new AlertDialog[1];

        for (int color : PRESET_COLORS) {
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(color);
            if ((int) effectiveColor == color) {
                circle.setStroke((int) (3 * density), 0xFFFFFFFF);
            }

            View swatch = new View(requireContext());
            swatch.setBackground(circle);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = swatchSize;
            params.height = swatchSize;
            params.setMargins(swatchMargin, swatchMargin, swatchMargin, swatchMargin);
            swatch.setLayoutParams(params);

            final int selectedColor = color;
            swatch.setOnClickListener(v -> {
                current.userCard.customColorPrimary = (long) selectedColor;
                viewModel.updateCard(current.userCard);
                binding.detailColorStrip.setBackgroundColor(selectedColor);
                if (dialogHolder[0] != null) dialogHolder[0].dismiss();
            });
            grid.addView(swatch);
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Card Color")
                .setView(grid)
                .setNeutralButton("Reset to Default", (d, w) -> {
                    current.userCard.customColorPrimary = null;
                    viewModel.updateCard(current.userCard);
                    binding.detailColorStrip.setBackgroundColor((int) current.definition.cardColorPrimary);
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialogHolder[0] = dialog;
        dialog.show();
    }

    private void showEditDialog() {
        UserCardWithDetails current = viewModel.getCardDetails().getValue();
        if (current == null) return;

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_card_details, null);
        TextInputEditText etNickname = dialogView.findViewById(R.id.et_dialog_nickname);
        TextInputEditText etLastFour = dialogView.findViewById(R.id.et_dialog_last_four);
        TextInputEditText etCreditLimit = dialogView.findViewById(R.id.et_dialog_credit_limit);
        TextInputEditText etOpenDate = dialogView.findViewById(R.id.et_dialog_open_date);

        if (current.userCard.nickname != null) etNickname.setText(current.userCard.nickname);
        if (current.userCard.lastFour != null) etLastFour.setText(current.userCard.lastFour);
        if (current.userCard.creditLimit > 0)
            etCreditLimit.setText(String.valueOf(current.userCard.creditLimit));

        final AtomicReference<LocalDate> selectedOpenDate =
                new AtomicReference<>(current.userCard.openDate);
        if (current.userCard.openDate != null)
            etOpenDate.setText(current.userCard.openDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        etOpenDate.setOnClickListener(v -> {
            LocalDate d = selectedOpenDate.get() != null ? selectedOpenDate.get() : LocalDate.now();
            new DatePickerDialog(requireContext(), (dp, y, m, day) -> {
                LocalDate picked = LocalDate.of(y, m + 1, day);
                selectedOpenDate.set(picked);
                etOpenDate.setText(picked.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }, d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth()).show();
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Edit Card")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String nickname = etNickname.getText() != null
                            ? etNickname.getText().toString().trim() : "";
                    String lastFour = etLastFour.getText() != null
                            ? etLastFour.getText().toString().trim() : "";
                    String limitStr = etCreditLimit.getText() != null
                            ? etCreditLimit.getText().toString().trim() : "";
                    int creditLimit = limitStr.isEmpty() ? 0 : Integer.parseInt(limitStr);

                    UserCard updated = current.userCard;
                    updated.nickname = nickname.isEmpty() ? null : nickname;
                    updated.lastFour = lastFour.isEmpty() ? null : lastFour;
                    updated.creditLimit = creditLimit;
                    updated.openDate = selectedOpenDate.get();
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

    private class SimpleRotationalBonusAdapter
            extends androidx.recyclerview.widget.RecyclerView.Adapter<SimpleRotationalBonusAdapter.VH> {

        private List<CardDetailViewModel.RotationalBonusInfo> items = new ArrayList<>();

        void setData(List<CardDetailViewModel.RotationalBonusInfo> data) {
            items = data != null ? data : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemRotationalBonusBannerBinding b = ItemRotationalBonusBannerBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CardDetailViewModel.RotationalBonusInfo info = items.get(position);
            com.example.ccrewards.data.model.RotationalBonus rb = info.bonus;

            holder.binding.dividerRb.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            holder.binding.tvRbCardName.setVisibility(View.GONE); // in card detail, no need for card name
            holder.binding.tvRbLabel.setText(rb.label != null ? rb.label : "");
            holder.binding.tvRbCategories.setText(info.categoryDisplay);

            if (rb.endDate != null) {
                holder.binding.tvRbEndDate.setText("Expires " + DateUtil.toDisplayString(rb.endDate));
                holder.binding.tvRbEndDate.setVisibility(View.VISIBLE);
            } else {
                holder.binding.tvRbEndDate.setVisibility(View.GONE);
            }

            int limitDollars = rb.spendLimitCents > 0 ? rb.spendLimitCents / 100 : 1500;
            holder.binding.seekbarRbUsage.setMax(limitDollars);
            int usedDollars = Math.min(rb.usedCents / 100, limitDollars);
            holder.binding.seekbarRbUsage.setProgress(usedDollars);
            updateUsedLabel(holder, rb.usedCents, rb.spendLimitCents);

            holder.binding.seekbarRbUsage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    seekBar.getParent().requestDisallowInterceptTouchEvent(true);
                }
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) return;
                    updateUsedLabel(holder, progress * 100, rb.spendLimitCents);
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int newUsedCents = seekBar.getProgress() * 100;
                    viewModel.updateRotationalBonusUsed(rb.id, newUsedCents, rb.spendLimitCents);
                }
            });

            holder.binding.btnRbMarkDone.setOnClickListener(v ->
                    viewModel.markRotationalBonusFullyUsed(rb.id));

            holder.binding.btnRbDelete.setOnClickListener(v ->
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Remove Quarterly Bonus")
                            .setMessage("Remove \"" + rb.label + "\"?")
                            .setPositiveButton("Remove", (d, w) ->
                                    viewModel.deleteRotationalBonus(rb.id))
                            .setNegativeButton("Cancel", null)
                            .show());
        }

        private void updateUsedLabel(VH holder, int usedCents, int limitCents) {
            String text = "$" + (usedCents / 100);
            if (limitCents > 0) text += " / $" + (limitCents / 100);
            holder.binding.tvRbUsedAmount.setText(text);
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            final ItemRotationalBonusBannerBinding binding;
            VH(ItemRotationalBonusBannerBinding b) {
                super(b.getRoot());
                binding = b;
            }
        }
    }

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
            holder.binding.tvBenefitAmount.setText(
                    CurrencyUtil.centsToString(benefit.amountCents) + "/" + periodSuffix(benefit.resetPeriod));
            holder.binding.chipBenefitPeriod.setText(formatPeriod(benefit.resetPeriod));
            holder.itemView.setOnClickListener(v -> {
                if (binding == null) return;
                Bundle args = new Bundle();
                args.putString("cardDefinitionId", benefit.cardDefinitionId);
                args.putLong("benefitId", benefit.id);
                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.action_cardDetail_to_addEditBenefit, args);
            });
        }

        private String periodSuffix(com.example.ccrewards.data.model.ResetPeriod period) {
            switch (period) {
                case MONTHLY: return "mo";
                case QUARTERLY: return "qtr";
                case SEMI_ANNUALLY: return "6mo";
                default: return "yr";
            }
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

    private void showEditHistoryDialog(ProductChangeRecord record) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Product Change Record")
                .setItems(new CharSequence[]{"Edit Date", "Edit Notes", "Delete"}, (dialog, which) -> {
                    if (which == 0) {
                        LocalDate d = record.changeDate != null ? record.changeDate : LocalDate.now();
                        new DatePickerDialog(requireContext(), (dp, y, m, day) -> {
                            record.changeDate = LocalDate.of(y, m + 1, day);
                            viewModel.updateProductChangeRecord(record);
                        }, d.getYear(), d.getMonthValue() - 1, d.getDayOfMonth()).show();
                    } else if (which == 1) {
                        android.widget.EditText etNotes = new android.widget.EditText(requireContext());
                        etNotes.setText(record.notes != null ? record.notes : "");
                        etNotes.setSingleLine(false);
                        int pad = (int) (16 * getResources().getDisplayMetrics().density);
                        etNotes.setPadding(pad, pad, pad, pad);
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Edit Notes")
                                .setView(etNotes)
                                .setPositiveButton("Save", (d2, w2) -> {
                                    String notes = etNotes.getText().toString().trim();
                                    record.notes = notes.isEmpty() ? null : notes;
                                    viewModel.updateProductChangeRecord(record);
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    } else {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Delete Record")
                                .setMessage("Remove this product change record?")
                                .setPositiveButton("Delete", (d2, w2) ->
                                        viewModel.deleteProductChangeRecord(record))
                                .setNegativeButton("Cancel", null)
                                .show();
                    }
                })
                .show();
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
            String desc = record.fromCardDefinitionId + " \u2192 " + record.toCardDefinitionId;
            if (record.notes != null && !record.notes.isEmpty()) desc += "\n" + record.notes;
            holder.binding.tvHistoryDescription.setText(desc);
            holder.itemView.setOnLongClickListener(v -> {
                showEditHistoryDialog(record);
                return true;
            });
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
