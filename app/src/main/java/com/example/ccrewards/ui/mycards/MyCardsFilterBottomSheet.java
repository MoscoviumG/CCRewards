package com.example.ccrewards.ui.mycards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleKt;

import com.example.ccrewards.R;
import com.example.ccrewards.databinding.BottomSheetMyCardsFilterBinding;
import com.example.ccrewards.ui.common.CardFilterState;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.HashSet;
import java.util.Set;

/** Bottom sheet for My Cards filter (5 dimensions: type, issuer, network, anniversary, card age). */
public class MyCardsFilterBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "MyCardsFilterBottomSheet";
    public static final String RESULT_KEY = "my_cards_filter_result";

    private static final String ARG_FILTER = "filter_state";

    private BottomSheetMyCardsFilterBinding binding;
    private CardFilterState currentState;

    public static MyCardsFilterBottomSheet newInstance(CardFilterState state) {
        MyCardsFilterBottomSheet sheet = new MyCardsFilterBottomSheet();
        Bundle args = new Bundle();
        // Serialize filter into the bundle
        args.putString("cardType", state.cardType.name());
        args.putStringArrayList("issuers", new java.util.ArrayList<>(state.issuers));
        args.putStringArrayList("networks", new java.util.ArrayList<>(state.networks));
        args.putString("anniversaryMonth", state.anniversaryMonth.name());
        args.putString("cardAge", state.cardAge.name());
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetMyCardsFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore state from args
        currentState = new CardFilterState();
        Bundle args = getArguments();
        if (args != null) {
            currentState.cardType = CardFilterState.CardType.valueOf(
                    args.getString("cardType", "ALL"));
            java.util.ArrayList<String> issuers = args.getStringArrayList("issuers");
            if (issuers != null) currentState.issuers = new HashSet<>(issuers);
            java.util.ArrayList<String> networks = args.getStringArrayList("networks");
            if (networks != null) currentState.networks = new HashSet<>(networks);
            currentState.anniversaryMonth = CardFilterState.AnniversaryFilter.valueOf(
                    args.getString("anniversaryMonth", "ANY"));
            currentState.cardAge = CardFilterState.CardAgeFilter.valueOf(
                    args.getString("cardAge", "ANY"));
        }

        // Populate chips from state
        applyStateToUi();

        // Clear all
        binding.btnClearFilters.setOnClickListener(v -> {
            currentState = new CardFilterState();
            applyStateToUi();
        });

        // Apply
        binding.btnApplyFilter.setOnClickListener(v -> {
            readStateFromUi();
            Bundle result = new Bundle();
            result.putString("cardType", currentState.cardType.name());
            result.putStringArrayList("issuers", new java.util.ArrayList<>(currentState.issuers));
            result.putStringArrayList("networks", new java.util.ArrayList<>(currentState.networks));
            result.putString("anniversaryMonth", currentState.anniversaryMonth.name());
            result.putString("cardAge", currentState.cardAge.name());
            getParentFragmentManager().setFragmentResult(RESULT_KEY, result);
            dismiss();
        });
    }

    private void applyStateToUi() {
        // Card type
        switch (currentState.cardType) {
            case PERSONAL: binding.chipTypePersonal.setChecked(true); break;
            case BUSINESS: binding.chipTypeBusiness.setChecked(true); break;
            default: binding.chipTypeAll.setChecked(true); break;
        }

        // Issuers
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

        // Networks
        binding.chipNetVisa.setChecked(currentState.networks.contains("Visa"));
        binding.chipNetMastercard.setChecked(currentState.networks.contains("Mastercard"));
        binding.chipNetAmex.setChecked(currentState.networks.contains("Amex"));
        binding.chipNetDiscover.setChecked(currentState.networks.contains("Discover"));

        // Anniversary
        switch (currentState.anniversaryMonth) {
            case THIS_MONTH: binding.chipAnnivThisMonth.setChecked(true); break;
            case NEXT_MONTH: binding.chipAnnivNextMonth.setChecked(true); break;
            default: binding.chipAnnivAny.setChecked(true); break;
        }

        // Card age
        switch (currentState.cardAge) {
            case LESS_THAN_1: binding.chipAgeLt1.setChecked(true); break;
            case ONE_TO_THREE: binding.chipAge1to3.setChecked(true); break;
            case MORE_THAN_THREE: binding.chipAge3plus.setChecked(true); break;
            default: binding.chipAgeAny.setChecked(true); break;
        }
    }

    private void readStateFromUi() {
        // Card type
        int typeId = binding.chipGroupType.getCheckedChipId();
        if (typeId == R.id.chip_type_personal) currentState.cardType = CardFilterState.CardType.PERSONAL;
        else if (typeId == R.id.chip_type_business) currentState.cardType = CardFilterState.CardType.BUSINESS;
        else currentState.cardType = CardFilterState.CardType.ALL;

        // Issuers
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

        // Networks
        currentState.networks = new HashSet<>();
        if (binding.chipNetVisa.isChecked())        currentState.networks.add("Visa");
        if (binding.chipNetMastercard.isChecked())  currentState.networks.add("Mastercard");
        if (binding.chipNetAmex.isChecked())        currentState.networks.add("Amex");
        if (binding.chipNetDiscover.isChecked())    currentState.networks.add("Discover");

        // Anniversary
        int annivId = binding.chipGroupAnniversary.getCheckedChipId();
        if (annivId == R.id.chip_anniv_this_month) currentState.anniversaryMonth = CardFilterState.AnniversaryFilter.THIS_MONTH;
        else if (annivId == R.id.chip_anniv_next_month) currentState.anniversaryMonth = CardFilterState.AnniversaryFilter.NEXT_MONTH;
        else currentState.anniversaryMonth = CardFilterState.AnniversaryFilter.ANY;

        // Card age
        int ageId = binding.chipGroupCardAge.getCheckedChipId();
        if (ageId == R.id.chip_age_lt1) currentState.cardAge = CardFilterState.CardAgeFilter.LESS_THAN_1;
        else if (ageId == R.id.chip_age_1to3) currentState.cardAge = CardFilterState.CardAgeFilter.ONE_TO_THREE;
        else if (ageId == R.id.chip_age_3plus) currentState.cardAge = CardFilterState.CardAgeFilter.MORE_THAN_THREE;
        else currentState.cardAge = CardFilterState.CardAgeFilter.ANY;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
