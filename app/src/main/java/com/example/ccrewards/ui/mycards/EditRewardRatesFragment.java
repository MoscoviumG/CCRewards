package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ccrewards.data.model.CustomCategory;
import com.example.ccrewards.data.model.CustomCategoryRate;
import com.example.ccrewards.data.model.PointValuation;
import com.example.ccrewards.data.model.RateType;
import com.example.ccrewards.data.model.RewardCategory;
import com.example.ccrewards.data.model.RewardRate;
import com.example.ccrewards.databinding.FragmentEditRewardRatesBinding;
import com.example.ccrewards.databinding.ItemRewardRateEditBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditRewardRatesFragment extends Fragment {

    private FragmentEditRewardRatesBinding binding;
    private EditRewardRatesViewModel viewModel;
    private RateEditAdapter adapter;
    private String cardDefinitionId;
    private String cardCurrencyName;   // card's default reward currency, loaded at init
    private boolean adapterInitialized = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEditRewardRatesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EditRewardRatesViewModel.class);

        cardDefinitionId = getArguments() != null ? getArguments().getString("cardDefinitionId") : null;

        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());

        adapter = new RateEditAdapter();
        adapter.setCurrencyPickerListener(this::showCurrencyPickerForRow);
        binding.recyclerEditRates.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerEditRates.setAdapter(adapter);
        binding.recyclerEditRates.setNestedScrollingEnabled(false);

        if (cardDefinitionId != null) {
            viewModel.loadRates(cardDefinitionId);
        }

        // Initialize adapter once on first LiveData emission
        viewModel.getRates().observe(getViewLifecycleOwner(), rates -> {
            if (!adapterInitialized && rates != null) {
                adapterInitialized = true;
                Executors.newSingleThreadExecutor().execute(() -> {
                    List<CustomCategoryRate> customRates = viewModel.getCustomRatesForCardSync();
                    List<CustomCategory> allCats = viewModel.getAllCustomCategoriesSync();
                    String cardCurrency = viewModel.getCardCurrencySync();
                    requireActivity().runOnUiThread(() -> {
                        cardCurrencyName = cardCurrency;
                        adapter.initRows(rates, customRates, allCats, cardCurrency);
                    });
                });
            }
        });

        // Save FAB
        binding.fabSaveRates.setOnClickListener(v -> {
            adapter.persistChanges(viewModel);
            Navigation.findNavController(v).navigateUp();
        });

        // Reset button
        binding.btnResetRates.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Reset Rates")
                        .setMessage("This will restore all reward rates to their defaults and remove any custom-category rate entries for this card. Continue?")
                        .setPositiveButton("Reset", (dialog, which) ->
                                viewModel.resetAllCustomizations(() ->
                                        requireActivity().runOnUiThread(() ->
                                                Navigation.findNavController(requireView()).navigateUp())))
                        .setNegativeButton("Cancel", null)
                        .show());

        // Add Category button
        binding.btnAddCategory.setOnClickListener(v -> showAddCategoryStep1());
    }

    // ── Add Category flow ─────────────────────────────────────────────────────

    private void showAddCategoryStep1() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CustomCategory> allCats = viewModel.getAllCustomCategoriesSync();
            List<PointValuation> valuations = viewModel.getAllValuationsSync();
            requireActivity().runOnUiThread(() -> {
                List<String> labels = new ArrayList<>();
                List<Object> values = new ArrayList<>(); // RewardCategory or CustomCategory

                // Collect currently used categories
                Set<String> usedStandard = new HashSet<>();
                Set<Long> usedCustom = new HashSet<>();
                for (RateEditAdapter.EditableRate row : adapter.getRows()) {
                    if (row.type == RateEditAdapter.EditableRate.Type.STANDARD
                            && row.rewardRate != null) {
                        usedStandard.add(row.rewardRate.category.name());
                    } else if (row.type == RateEditAdapter.EditableRate.Type.CUSTOM
                            && row.customRate != null) {
                        usedCustom.add(row.customRate.customCategoryId);
                    }
                }

                // Available standard categories
                for (RewardCategory cat : RewardCategory.values()) {
                    if (!usedStandard.contains(cat.name())) {
                        labels.add(formatCategory(cat.name()));
                        values.add(cat);
                    }
                }

                // Available custom categories
                for (CustomCategory cat : allCats) {
                    if (!usedCustom.contains(cat.id)) {
                        labels.add(cat.name + " (Custom)");
                        values.add(cat);
                    }
                }

                if (labels.isEmpty()) {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("No more categories")
                            .setMessage("All available categories already have a rate for this card.")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                final int[] selectedIdx = {0};
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Select Category")
                        .setSingleChoiceItems(labels.toArray(new String[0]), 0,
                                (d, which) -> selectedIdx[0] = which)
                        .setPositiveButton("Next", (d, w) ->
                                showAddCategoryStep2(values.get(selectedIdx[0]), valuations))
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });
    }

    private void showAddCategoryStep2(Object selectedCategory, List<PointValuation> valuations) {
        int padPx = (int) (16 * getResources().getDisplayMetrics().density);
        String categoryLabel = selectedCategory instanceof RewardCategory
                ? formatCategory(((RewardCategory) selectedCategory).name())
                : ((CustomCategory) selectedCategory).name;

        EditText etRate = new EditText(requireContext());
        etRate.setHint("Rate (e.g. 3)");
        etRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (selectedCategory instanceof RewardCategory) {
            // STANDARD: show rate + currency picker Spinner
            List<String> currencyNames = new ArrayList<>();
            for (PointValuation pv : valuations) currencyNames.add(pv.rewardCurrencyName);
            final String CREATE_NEW = "Create new currency...";
            currencyNames.add(CREATE_NEW);
            final int[] currencyIdx = {0};

            android.widget.Spinner spinner = new android.widget.Spinner(requireContext());
            android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(
                    requireContext(), android.R.layout.simple_spinner_item, currencyNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> a, View v, int pos, long id) {
                    currencyIdx[0] = pos;
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> a) {}
            });

            android.widget.LinearLayout container = new android.widget.LinearLayout(requireContext());
            container.setOrientation(android.widget.LinearLayout.VERTICAL);
            container.setPadding(padPx, padPx / 2, padPx, 0);
            container.addView(etRate);
            container.addView(spinner);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Rate for " + categoryLabel)
                    .setView(container)
                    .setPositiveButton("Add", (d, w) -> {
                        String rateStr = etRate.getText() != null ? etRate.getText().toString().trim() : "";
                        if (rateStr.isEmpty()) return;
                        try {
                            double rate = Double.parseDouble(rateStr);
                            String selectedCurrency = currencyNames.get(currencyIdx[0]);
                            if (CREATE_NEW.equals(selectedCurrency)) {
                                showCreateCurrencyDialog(name -> addStandardRowWithWarning(
                                        (RewardCategory) selectedCategory, categoryLabel, rate, name));
                            } else {
                                addStandardRowWithWarning(
                                        (RewardCategory) selectedCategory, categoryLabel, rate, selectedCurrency);
                            }
                        } catch (NumberFormatException ignored) {}
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            // CUSTOM: show rate + currency picker
            CustomCategory cat = (CustomCategory) selectedCategory;
            android.widget.LinearLayout container = new android.widget.LinearLayout(requireContext());
            container.setOrientation(android.widget.LinearLayout.VERTICAL);
            container.setPadding(padPx, padPx / 2, padPx, 0);
            container.addView(etRate);

            // Build currency list: valuations + "Create new..."
            List<String> currencyNames = new ArrayList<>();
            for (PointValuation pv : valuations) currencyNames.add(pv.rewardCurrencyName);
            final String CREATE_NEW = "Create new currency...";
            currencyNames.add(CREATE_NEW);
            final int[] currencyIdx = {0};

            android.widget.Spinner spinner = new android.widget.Spinner(requireContext());
            android.widget.ArrayAdapter<String> spinnerAdapter = new android.widget.ArrayAdapter<>(
                    requireContext(), android.R.layout.simple_spinner_item, currencyNames);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override public void onItemSelected(android.widget.AdapterView<?> a, View v, int pos, long id) {
                    currencyIdx[0] = pos;
                }
                @Override public void onNothingSelected(android.widget.AdapterView<?> a) {}
            });
            container.addView(spinner);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Rate for " + categoryLabel)
                    .setView(container)
                    .setPositiveButton("Add", (d, w) -> {
                        String rateStr = etRate.getText() != null ? etRate.getText().toString().trim() : "";
                        if (rateStr.isEmpty()) return;
                        try {
                            double rate = Double.parseDouble(rateStr);
                            String selectedCurrency = currencyNames.get(currencyIdx[0]);
                            if (CREATE_NEW.equals(selectedCurrency)) {
                                showCreateCurrencyDialog(name -> {
                                    RateType rt = rateTypeFromCurrency(name);
                                    CustomCategoryRate ccr = new CustomCategoryRate(
                                            cat.id, cardDefinitionId, rate, rt, name);
                                    adapter.addCustomRow(ccr, cat);
                                });
                            } else {
                                RateType rt = rateTypeFromCurrency(selectedCurrency);
                                CustomCategoryRate ccr = new CustomCategoryRate(
                                        cat.id, cardDefinitionId, rate, rt, selectedCurrency);
                                adapter.addCustomRow(ccr, cat);
                            }
                        } catch (NumberFormatException ignored) {}
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    /** Shows a "create new currency" dialog, then calls onCreated with the new name. */
    private void showCreateCurrencyDialog(java.util.function.Consumer<String> onCreated) {
        int padPx = (int) (16 * getResources().getDisplayMetrics().density);

        TextInputLayout tilName = new TextInputLayout(requireContext());
        tilName.setHint("Currency name (e.g. Hilton Honors Points)");
        TextInputEditText etName = new TextInputEditText(requireContext());
        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        etName.setSingleLine(true);
        tilName.addView(etName);

        TextInputLayout tilCpp = new TextInputLayout(requireContext());
        tilCpp.setHint("Value in ¢/pt (e.g. 0.5)");
        TextInputEditText etCpp = new TextInputEditText(requireContext());
        etCpp.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etCpp.setSingleLine(true);
        tilCpp.addView(etCpp);

        android.widget.LinearLayout container = new android.widget.LinearLayout(requireContext());
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        container.setPadding(padPx, 0, padPx, 0);
        container.addView(tilName);
        container.addView(tilCpp);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Create Currency")
                .setView(container)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = etName.getText() != null
                            ? etName.getText().toString().trim() : "";
                    if (name.isEmpty()) return;
                    String cppStr = etCpp.getText() != null
                            ? etCpp.getText().toString().trim() : "";
                    double cpp = cppStr.isEmpty() ? 1.0 : Double.parseDouble(cppStr);
                    viewModel.insertValuationSync(
                            new PointValuation(name, cpp, cpp),
                            () -> requireActivity().runOnUiThread(() -> onCreated.accept(name)));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Shows currency picker for any row (STANDARD or CUSTOM). */
    private void showCurrencyPickerForRow(RateEditAdapter.EditableRate row) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<PointValuation> valuations = viewModel.getAllValuationsSync();
            requireActivity().runOnUiThread(() -> {
                List<String> names = new ArrayList<>();
                for (PointValuation pv : valuations) names.add(pv.rewardCurrencyName);
                final String CREATE_NEW = "Create new currency...";
                names.add(CREATE_NEW);

                int currentIdx = 0;
                if (!row.currencyName.isEmpty()) {
                    int idx = names.indexOf(row.currencyName);
                    if (idx >= 0) currentIdx = idx;
                }

                final int[] selected = {currentIdx};
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Select Currency")
                        .setSingleChoiceItems(names.toArray(new String[0]), currentIdx,
                                (d, which) -> selected[0] = which)
                        .setPositiveButton("Select", (d, w) -> {
                            String chosenName = names.get(selected[0]);
                            if (CREATE_NEW.equals(chosenName)) {
                                showCreateCurrencyDialog(name -> applyPickedCurrency(row, name));
                            } else {
                                applyPickedCurrency(row, chosenName);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });
    }

    private void applyPickedCurrency(RateEditAdapter.EditableRate row, String name) {
        if (row.type == RateEditAdapter.EditableRate.Type.STANDARD
                && cardCurrencyName != null
                && !cardCurrencyName.isEmpty()
                && !cardCurrencyName.equals(name)) {
            // Warn user that this differs from the card's default currency
            String categoryLabel = row.displayName;
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Different currency")
                    .setMessage("You selected \"" + name + "\" for " + categoryLabel
                            + ", but this card's default currency is \"" + cardCurrencyName
                            + "\". The effective return for this category will be calculated using \""
                            + name + "\". Continue?")
                    .setPositiveButton("Continue", (d, w) -> doApply(row, name))
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            doApply(row, name);
        }
    }

    private void addStandardRowWithWarning(RewardCategory category, String categoryLabel,
                                           double rate, String currency) {
        Runnable commit = () -> {
            RewardRate newRate = new RewardRate(cardDefinitionId, category,
                    rateTypeFromCurrency(currency), rate);
            newRate.isCustomized = true;
            adapter.addStandardRow(newRate, currency);
        };
        if (cardCurrencyName != null && !cardCurrencyName.isEmpty()
                && !cardCurrencyName.equals(currency)) {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Different currency")
                    .setMessage("You selected \"" + currency + "\" for " + categoryLabel
                            + ", but this card's default currency is \"" + cardCurrencyName
                            + "\". The effective return will be calculated using \""
                            + currency + "\". Continue?")
                    .setPositiveButton("Continue", (d, w) -> commit.run())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            commit.run();
        }
    }

    private void doApply(RateEditAdapter.EditableRate row, String name) {
        row.currencyName = name;
        row.rateType = rateTypeFromCurrency(name);
        if (row.customRate != null) {
            row.customRate.currencyName = name;
            row.customRate.rateType = row.rateType;
        }
        adapter.notifyDataSetChanged();
    }

    private static String defaultCurrencyLabel(RateType rateType) {
        if (rateType == null) return "Points";
        switch (rateType) {
            case CASHBACK: return "Cash Back";
            case MILES: return "Miles";
            default: return "Points";
        }
    }

    static RateType rateTypeFromCurrency(String currencyName) {
        if (currencyName == null || currencyName.isEmpty()) return RateType.POINTS;
        String lower = currencyName.toLowerCase(java.util.Locale.US);
        if (lower.contains("cash")) return RateType.CASHBACK;
        if (lower.contains("mile")) return RateType.MILES;
        return RateType.POINTS;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    static String formatCategory(String enumName) {
        if (enumName == null) return "";
        switch (enumName) {
            case "TRAVEL":                return "General Travel";
            case "TRAVEL_PORTAL":         return "Travel Portal";
            case "TRAVEL_HILTON":         return "Hilton";
            case "TRAVEL_MARRIOTT":       return "Marriott";
            case "TRAVEL_IHG":            return "IHG";
            case "TRAVEL_HYATT":          return "Hyatt";
            case "TRAVEL_DELTA":          return "Delta";
            case "TRAVEL_UNITED":         return "United";
            case "TRAVEL_SOUTHWEST":      return "Southwest";
            case "TRAVEL_AA":             return "American Airlines";
            case "TRAVEL_AEROPLAN":       return "Aeroplan";
            case "TRAVEL_BRITISH_AIRWAYS":return "British Airways";
            case "TRAVEL_AER_LINGUS":     return "Aer Lingus";
            case "TRAVEL_IBERIA":         return "Iberia";
            case "TRAVEL_AIR_FRANCE_KLM": return "Air France / KLM";
            case "TRAVEL_SPIRIT":         return "Spirit";
            case "TRAVEL_ALLEGIANT":      return "Allegiant";
            case "TRAVEL_ALASKA":         return "Alaska Airlines";
            case "TRAVEL_CRUISES":        return "Cruises";
            default:
                String[] parts = enumName.split("_");
                StringBuilder sb = new StringBuilder();
                for (String part : parts) {
                    if (sb.length() > 0) sb.append(" ");
                    sb.append(Character.toUpperCase(part.charAt(0)))
                      .append(part.substring(1).toLowerCase());
                }
                return sb.toString();
        }
    }

    // ── Rate Edit Adapter ─────────────────────────────────────────────────────

    static class RateEditAdapter extends RecyclerView.Adapter<RateEditAdapter.VH> {

        interface CurrencyPickerListener {
            void onChangeCurrencyClick(EditableRate row);
        }

        static class EditableRate {
            enum Type { STANDARD, CUSTOM }
            Type type;
            @Nullable RewardRate rewardRate;          // non-null for STANDARD
            @Nullable CustomCategoryRate customRate;   // non-null for CUSTOM
            @Nullable CustomCategory customCategory;   // display name for CUSTOM
            String displayName;
            String currencyName = "";
            double rate;
            RateType rateType;
            boolean isNew;
        }

        private final List<EditableRate> rows = new ArrayList<>();
        private final List<EditableRate> deletedRows = new ArrayList<>();
        @Nullable private CurrencyPickerListener currencyPickerListener;

        void setCurrencyPickerListener(CurrencyPickerListener listener) {
            this.currencyPickerListener = listener;
        }

        void initRows(List<RewardRate> standardRates,
                      List<CustomCategoryRate> customRates,
                      List<CustomCategory> allCats,
                      String cardCurrencyName) {
            rows.clear();
            deletedRows.clear();

            for (RewardRate rr : standardRates) {
                EditableRate row = new EditableRate();
                row.type = EditableRate.Type.STANDARD;
                row.rewardRate = rr;
                row.displayName = EditRewardRatesFragment.formatCategory(rr.category.name());
                row.currencyName = (rr.currencyName != null && !rr.currencyName.isEmpty())
                        ? rr.currencyName
                        : (rr.rateType == RateType.CASHBACK
                                ? "Cash Back"
                                : (cardCurrencyName != null ? cardCurrencyName : defaultCurrencyLabel(rr.rateType)));
                row.rate = rr.rate;
                row.rateType = rr.rateType;
                rows.add(row);
            }

            Map<Long, CustomCategory> catMap = new HashMap<>();
            for (CustomCategory cat : allCats) catMap.put(cat.id, cat);

            for (CustomCategoryRate ccr : customRates) {
                EditableRate row = new EditableRate();
                row.type = EditableRate.Type.CUSTOM;
                row.customRate = ccr;
                row.customCategory = catMap.get(ccr.customCategoryId);
                row.displayName = row.customCategory != null
                        ? row.customCategory.name : "Custom #" + ccr.customCategoryId;
                row.currencyName = ccr.currencyName;
                row.rate = ccr.rate;
                row.rateType = ccr.rateType;
                rows.add(row);
            }
            notifyDataSetChanged();
        }

        void addStandardRow(RewardRate rr, String currencyName) {
            EditableRate row = new EditableRate();
            row.type = EditableRate.Type.STANDARD;
            row.rewardRate = rr;
            row.displayName = EditRewardRatesFragment.formatCategory(rr.category.name());
            row.currencyName = currencyName != null ? currencyName : defaultCurrencyLabel(rr.rateType);
            row.rate = rr.rate;
            row.rateType = rr.rateType;
            row.isNew = true;
            rows.add(row);
            notifyItemInserted(rows.size() - 1);
        }

        void addCustomRow(CustomCategoryRate ccr, CustomCategory cat) {
            EditableRate row = new EditableRate();
            row.type = EditableRate.Type.CUSTOM;
            row.customRate = ccr;
            row.customCategory = cat;
            row.displayName = cat.name;
            row.currencyName = ccr.currencyName;
            row.rate = ccr.rate;
            row.rateType = ccr.rateType;
            row.isNew = true;
            rows.add(row);
            notifyItemInserted(rows.size() - 1);
        }

        List<EditableRate> getRows() {
            return rows;
        }

        void persistChanges(EditRewardRatesViewModel viewModel) {
            for (EditableRate row : deletedRows) {
                if (row.type == EditableRate.Type.STANDARD && row.rewardRate != null && !row.isNew) {
                    viewModel.deleteStandardRate(row.rewardRate);
                } else if (row.type == EditableRate.Type.CUSTOM && row.customRate != null && !row.isNew) {
                    viewModel.deleteCustomRate(row.customRate);
                }
            }
            for (EditableRate row : rows) {
                if (row.type == EditableRate.Type.STANDARD) {
                    if (row.isNew) {
                        viewModel.insertStandardRate(
                                row.rewardRate.category, row.rate, row.rateType, row.currencyName);
                    } else {
                        row.rewardRate.rate = row.rate;
                        row.rewardRate.rateType = row.rateType;
                        row.rewardRate.currencyName = row.currencyName;
                        viewModel.saveRate(row.rewardRate);
                    }
                } else if (row.type == EditableRate.Type.CUSTOM && row.customRate != null) {
                    row.customRate.rate = row.rate;
                    row.customRate.rateType = row.rateType;
                    viewModel.saveCustomRate(row.customRate);
                }
            }
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemRewardRateEditBinding b = ItemRewardRateEditBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new VH(b);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.bind(rows.get(position));
        }

        @Override
        public int getItemCount() { return rows.size(); }

        class VH extends RecyclerView.ViewHolder {
            final ItemRewardRateEditBinding binding;

            VH(ItemRewardRateEditBinding b) {
                super(b.getRoot());
                binding = b;
            }

            void bind(EditableRate row) {
                binding.tvEditCategory.setText(row.displayName);

                // Rate value — avoid triggering watcher during bind
                String rateStr = row.rate == Math.floor(row.rate)
                        ? String.valueOf((int) row.rate)
                        : String.valueOf(row.rate);
                if (!rateStr.equals(binding.etEditRate.getText() != null
                        ? binding.etEditRate.getText().toString() : "")) {
                    binding.etEditRate.setText(rateStr);
                }

                // Currency picker for all rows
                String currencyLabel = !row.currencyName.isEmpty() ? row.currencyName : "No currency set";
                binding.tvSelectedCurrency.setText(currencyLabel);
                binding.btnChangeCurrency.setOnClickListener(v -> {
                    if (currencyPickerListener != null) currencyPickerListener.onChangeCurrencyClick(row);
                });

                // Rate value watcher
                binding.etEditRate.addTextChangedListener(new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                    @Override public void afterTextChanged(Editable s) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try { row.rate = Double.parseDouble(s.toString()); }
                        catch (NumberFormatException ignored) {}
                    }
                });

                // Badges
                binding.chipChoiceCategory.setVisibility(
                        row.type == EditableRate.Type.STANDARD
                                && row.rewardRate != null
                                && row.rewardRate.isChoiceCategory ? View.VISIBLE : View.GONE);
                binding.tvCustomizedLabel.setVisibility(
                        row.type == EditableRate.Type.STANDARD
                                && row.rewardRate != null
                                && row.rewardRate.isCustomized ? View.VISIBLE : View.GONE);

                // Delete button
                binding.btnDeleteRate.setOnClickListener(v -> {
                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_ID || pos >= rows.size()) return;
                    EditableRate removed = rows.remove(pos);
                    deletedRows.add(removed);
                    notifyItemRemoved(pos);
                });
            }
        }
    }
}
