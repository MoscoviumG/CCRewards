package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.text.Editable;
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

import com.example.ccrewards.R;
import com.example.ccrewards.data.model.CardDefinition;
import com.example.ccrewards.databinding.FragmentAddCardBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;

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

        // Card click → show add-details dialog
        catalogAdapter.setOnItemClickListener(new CatalogAdapter.OnItemClickListener() {
            @Override
            public void onCardClick(CardDefinition card) {
                showAddCardDialog(card);
            }

            @Override
            public void onCreateCustomClick() {
                showCreateCustomDialog();
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

        // Filter chips
        binding.filterChipGroupAdd.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_add_all) viewModel.setFilter(AddCardViewModel.Filter.ALL);
            else if (id == R.id.chip_add_personal) viewModel.setFilter(AddCardViewModel.Filter.PERSONAL);
            else if (id == R.id.chip_add_business) viewModel.setFilter(AddCardViewModel.Filter.BUSINESS);
        });
    }

    private void showAddCardDialog(CardDefinition card) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_card_details, null);

        TextInputEditText etNickname = dialogView.findViewById(R.id.et_dialog_nickname);
        TextInputEditText etCreditLimit = dialogView.findViewById(R.id.et_dialog_credit_limit);
        TextInputEditText etOpenDate = dialogView.findViewById(R.id.et_dialog_open_date);

        final AtomicReference<LocalDate> selectedDate = new AtomicReference<>(LocalDate.now());
        etOpenDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        etOpenDate.setFocusable(false);
        etOpenDate.setOnClickListener(v -> showDatePicker(etOpenDate, selectedDate));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add " + card.displayName)
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String nickname = etNickname.getText() != null
                            ? etNickname.getText().toString().trim() : "";
                    String limitStr = etCreditLimit.getText() != null
                            ? etCreditLimit.getText().toString().trim() : "";
                    int creditLimit = limitStr.isEmpty() ? 0 : Integer.parseInt(limitStr);
                    LocalDate openDate = selectedDate.get() != null ? selectedDate.get() : LocalDate.now();

                    viewModel.addUserCard(card, nickname, creditLimit, openDate, () ->
                            requireActivity().runOnUiThread(() ->
                                    Navigation.findNavController(requireView()).navigateUp()));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCreateCustomDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_create_custom_card, null);

        TextInputEditText etName = dialogView.findViewById(R.id.et_custom_name);
        TextInputEditText etIssuer = dialogView.findViewById(R.id.et_custom_issuer);
        TextInputEditText etFee = dialogView.findViewById(R.id.et_custom_fee);
        TextInputEditText etNickname = dialogView.findViewById(R.id.et_custom_nickname);
        TextInputEditText etCreditLimit = dialogView.findViewById(R.id.et_custom_credit_limit);
        TextInputEditText etOpenDate = dialogView.findViewById(R.id.et_custom_open_date);

        final AtomicReference<LocalDate> selectedDate = new AtomicReference<>(LocalDate.now());
        etOpenDate.setText(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        etOpenDate.setFocusable(false);
        etOpenDate.setOnClickListener(v -> showDatePicker(etOpenDate, selectedDate));

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Create Custom Card")
                .setView(dialogView)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = etName.getText() != null ? etName.getText().toString().trim() : "Custom Card";
                    String issuer = etIssuer.getText() != null ? etIssuer.getText().toString().trim() : "Custom";
                    String feeStr = etFee.getText() != null ? etFee.getText().toString().trim() : "0";
                    int fee = feeStr.isEmpty() ? 0 : Integer.parseInt(feeStr);
                    String nickname = etNickname.getText() != null ? etNickname.getText().toString().trim() : "";
                    String limitStr = etCreditLimit.getText() != null ? etCreditLimit.getText().toString().trim() : "";
                    int creditLimit = limitStr.isEmpty() ? 0 : Integer.parseInt(limitStr);
                    LocalDate openDate = selectedDate.get() != null ? selectedDate.get() : LocalDate.now();

                    if (!name.isEmpty()) {
                        viewModel.createCustomCard(name, issuer, fee, nickname, creditLimit, openDate,
                                () -> requireActivity().runOnUiThread(() ->
                                        Navigation.findNavController(requireView()).navigateUp()));
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDatePicker(TextInputEditText etTarget, AtomicReference<LocalDate> ref) {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select open date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            LocalDate date = Instant.ofEpochMilli(selection)
                    .atZone(ZoneId.of("UTC")).toLocalDate();
            ref.set(date);
            etTarget.setText(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        });
        picker.show(getChildFragmentManager(), "date_picker");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
