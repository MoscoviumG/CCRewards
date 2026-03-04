package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ccrewards.R;
import com.example.ccrewards.databinding.BottomSheetAddCardFilterBinding;
import com.example.ccrewards.ui.common.CardFilterState;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashSet;

/** Bottom sheet for Add Card filter (3 dimensions: type, issuer, network). */
public class AddCardFilterBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "AddCardFilterBottomSheet";
    public static final String RESULT_KEY = "add_card_filter_result";

    private BottomSheetAddCardFilterBinding binding;
    private CardFilterState currentState;

    public static AddCardFilterBottomSheet newInstance(CardFilterState state) {
        AddCardFilterBottomSheet sheet = new AddCardFilterBottomSheet();
        Bundle args = new Bundle();
        args.putString("cardType", state.cardType.name());
        args.putStringArrayList("issuers", new java.util.ArrayList<>(state.issuers));
        args.putStringArrayList("networks", new java.util.ArrayList<>(state.networks));
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddCardFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentState = new CardFilterState();
        Bundle args = getArguments();
        if (args != null) {
            currentState.cardType = CardFilterState.CardType.valueOf(
                    args.getString("cardType", "ALL"));
            java.util.ArrayList<String> issuers = args.getStringArrayList("issuers");
            if (issuers != null) currentState.issuers = new HashSet<>(issuers);
            java.util.ArrayList<String> networks = args.getStringArrayList("networks");
            if (networks != null) currentState.networks = new HashSet<>(networks);
        }

        applyStateToUi();

        binding.btnClearFilters.setOnClickListener(v -> {
            currentState = new CardFilterState();
            applyStateToUi();
        });

        binding.btnApplyFilter.setOnClickListener(v -> {
            readStateFromUi();
            Bundle result = new Bundle();
            result.putString("cardType", currentState.cardType.name());
            result.putStringArrayList("issuers", new java.util.ArrayList<>(currentState.issuers));
            result.putStringArrayList("networks", new java.util.ArrayList<>(currentState.networks));
            getParentFragmentManager().setFragmentResult(RESULT_KEY, result);
            dismiss();
        });
    }

    private void applyStateToUi() {
        switch (currentState.cardType) {
            case PERSONAL: binding.chipTypePersonal.setChecked(true); break;
            case BUSINESS: binding.chipTypeBusiness.setChecked(true); break;
            default: binding.chipTypeAll.setChecked(true); break;
        }
        binding.chipIssuerChase.setChecked(currentState.issuers.contains("Chase"));
        binding.chipIssuerAmex.setChecked(currentState.issuers.contains("American Express"));
        binding.chipIssuerCapitalOne.setChecked(currentState.issuers.contains("Capital One"));
        binding.chipIssuerCiti.setChecked(currentState.issuers.contains("Citi"));
        binding.chipIssuerBofa.setChecked(currentState.issuers.contains("Bank of America"));
        binding.chipIssuerUsBank.setChecked(currentState.issuers.contains("U.S. Bank"));
        binding.chipIssuerDiscover.setChecked(currentState.issuers.contains("Discover"));
        binding.chipIssuerWellsFargo.setChecked(currentState.issuers.contains("Wells Fargo"));
        binding.chipIssuerBilt.setChecked(currentState.issuers.contains("Bilt"));
        binding.chipIssuerApple.setChecked(currentState.issuers.contains("Apple"));

        binding.chipNetVisa.setChecked(currentState.networks.contains("Visa"));
        binding.chipNetMastercard.setChecked(currentState.networks.contains("Mastercard"));
        binding.chipNetAmex.setChecked(currentState.networks.contains("Amex"));
        binding.chipNetDiscover.setChecked(currentState.networks.contains("Discover"));
    }

    private void readStateFromUi() {
        int typeId = binding.chipGroupType.getCheckedChipId();
        if (typeId == R.id.chip_type_personal) currentState.cardType = CardFilterState.CardType.PERSONAL;
        else if (typeId == R.id.chip_type_business) currentState.cardType = CardFilterState.CardType.BUSINESS;
        else currentState.cardType = CardFilterState.CardType.ALL;

        currentState.issuers = new HashSet<>();
        if (binding.chipIssuerChase.isChecked())     currentState.issuers.add("Chase");
        if (binding.chipIssuerAmex.isChecked())      currentState.issuers.add("American Express");
        if (binding.chipIssuerCapitalOne.isChecked()) currentState.issuers.add("Capital One");
        if (binding.chipIssuerCiti.isChecked())      currentState.issuers.add("Citi");
        if (binding.chipIssuerBofa.isChecked())      currentState.issuers.add("Bank of America");
        if (binding.chipIssuerUsBank.isChecked())    currentState.issuers.add("U.S. Bank");
        if (binding.chipIssuerDiscover.isChecked())  currentState.issuers.add("Discover");
        if (binding.chipIssuerWellsFargo.isChecked()) currentState.issuers.add("Wells Fargo");
        if (binding.chipIssuerBilt.isChecked())      currentState.issuers.add("Bilt");
        if (binding.chipIssuerApple.isChecked())     currentState.issuers.add("Apple");

        currentState.networks = new HashSet<>();
        if (binding.chipNetVisa.isChecked())       currentState.networks.add("Visa");
        if (binding.chipNetMastercard.isChecked()) currentState.networks.add("Mastercard");
        if (binding.chipNetAmex.isChecked())       currentState.networks.add("Amex");
        if (binding.chipNetDiscover.isChecked())   currentState.networks.add("Discover");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
