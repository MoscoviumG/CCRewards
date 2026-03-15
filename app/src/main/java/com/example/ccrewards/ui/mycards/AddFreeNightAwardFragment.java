package com.example.ccrewards.ui.mycards;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.ccrewards.data.model.FreeNightAward;
import com.example.ccrewards.data.model.FreeNightLimitType;
import com.example.ccrewards.data.model.HotelGroup;
import com.example.ccrewards.databinding.FragmentAddFreeNightAwardBinding;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddFreeNightAwardFragment extends Fragment {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US);

    private FragmentAddFreeNightAwardBinding binding;
    private AddFreeNightAwardViewModel viewModel;
    private long userCardId;
    private long awardId;
    private final AtomicReference<LocalDate> selectedExpiry = new AtomicReference<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddFreeNightAwardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AddFreeNightAwardViewModel.class);

        userCardId = getArguments() != null ? getArguments().getLong("userCardId", -1) : -1;
        awardId    = getArguments() != null ? getArguments().getLong("awardId", -1) : -1;

        boolean editMode = awardId != -1;
        binding.toolbar.setTitle(editMode ? "Edit Free Night" : "Add Free Night");
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());
        binding.btnSaveFreeNight.setText(editMode ? "Save Changes" : "Save Free Night");

        // Toggle fields based on limit type selection
        binding.chipGroupLimitType.setOnCheckedStateChangeListener(
                (group, ids) -> updateLimitTypeVisibility());

        // Default chips — may be overridden when existing award loads
        binding.chipHotelHilton.setChecked(true);
        binding.chipLimitUnlimited.setChecked(true);
        updateLimitTypeVisibility();

        // Expiry date picker
        binding.etFnExpiry.setOnClickListener(v -> {
            LocalDate base = selectedExpiry.get() != null
                    ? selectedExpiry.get() : LocalDate.now().plusYears(1);
            new DatePickerDialog(requireContext(), (dp, y, m, day) -> {
                LocalDate picked = LocalDate.of(y, m + 1, day);
                selectedExpiry.set(picked);
                binding.etFnExpiry.setText(picked.format(DATE_FMT));
            }, base.getYear(), base.getMonthValue() - 1, base.getDayOfMonth()).show();
        });

        if (editMode) {
            viewModel.loadAward(awardId);
            viewModel.getExistingAward().observe(getViewLifecycleOwner(), this::populateFromAward);
        }

        binding.btnSaveFreeNight.setOnClickListener(v -> onSave());
    }

    private void populateFromAward(FreeNightAward award) {
        if (award == null) return;

        // Expiry
        if (award.expirationDate != null) {
            selectedExpiry.set(award.expirationDate);
            binding.etFnExpiry.setText(award.expirationDate.format(DATE_FMT));
        }

        // Count
        binding.etFnCount.setText(String.valueOf(award.totalCount));

        // Label
        if (award.label != null) binding.etFnLabel.setText(award.label);

        // Reverse-parse typeKey to select correct chips
        AddFreeNightAwardViewModel.ParsedTypeKey parsed =
                AddFreeNightAwardViewModel.parseTypeKey(award.typeKey);
        if (parsed != null) {
            selectHotelChip(parsed.hotelGroup);
            selectLimitChip(parsed.limitType);
            if (parsed.pointsCap != null)
                binding.etPointsCap.setText(String.valueOf(parsed.pointsCap));
            if (parsed.hyattCategory != null)
                binding.etHyattCategory.setText(String.valueOf(parsed.hyattCategory));
            updateLimitTypeVisibility();
        }
    }

    private void selectHotelChip(HotelGroup group) {
        binding.chipHotelHilton.setChecked(group == HotelGroup.HILTON);
        binding.chipHotelMarriott.setChecked(group == HotelGroup.MARRIOTT);
        binding.chipHotelIhg.setChecked(group == HotelGroup.IHG);
        binding.chipHotelHyatt.setChecked(group == HotelGroup.HYATT);
        binding.chipHotelWyndham.setChecked(group == HotelGroup.WYNDHAM);
        binding.chipHotelRadisson.setChecked(group == HotelGroup.RADISSON);
        binding.chipHotelChoice.setChecked(group == HotelGroup.CHOICE);
        binding.chipHotelBestWestern.setChecked(group == HotelGroup.BEST_WESTERN);
    }

    private void selectLimitChip(FreeNightLimitType limitType) {
        binding.chipLimitUnlimited.setChecked(limitType == FreeNightLimitType.UNLIMITED);
        binding.chipLimitPointsCap.setChecked(limitType == FreeNightLimitType.POINTS_CAP);
        binding.chipLimitHyattCat.setChecked(limitType == FreeNightLimitType.HYATT_CATEGORY);
    }

    private void updateLimitTypeVisibility() {
        boolean isPointsCap = binding.chipLimitPointsCap.isChecked();
        boolean isHyattCat  = binding.chipLimitHyattCat.isChecked();
        binding.tilPointsCap.setVisibility(isPointsCap ? View.VISIBLE : View.GONE);
        binding.tilHyattCategory.setVisibility(isHyattCat ? View.VISIBLE : View.GONE);
    }

    private void onSave() {
        HotelGroup hotelGroup = getSelectedHotelGroup();
        if (hotelGroup == null) {
            Snackbar.make(binding.getRoot(), "Please select a hotel program",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        FreeNightLimitType limitType = getSelectedLimitType();
        if (limitType == null) {
            Snackbar.make(binding.getRoot(), "Please select a certificate type",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        Integer pointsCap = null;
        if (limitType == FreeNightLimitType.POINTS_CAP) {
            String capStr = binding.etPointsCap.getText() != null
                    ? binding.etPointsCap.getText().toString().trim() : "";
            if (capStr.isEmpty()) { binding.tilPointsCap.setError("Required"); return; }
            try { pointsCap = Integer.parseInt(capStr); }
            catch (NumberFormatException e) { binding.tilPointsCap.setError("Invalid"); return; }
        }

        Integer hyattCategory = null;
        if (limitType == FreeNightLimitType.HYATT_CATEGORY) {
            String catStr = binding.etHyattCategory.getText() != null
                    ? binding.etHyattCategory.getText().toString().trim() : "";
            if (catStr.isEmpty()) { binding.tilHyattCategory.setError("Required (1–7)"); return; }
            try {
                hyattCategory = Integer.parseInt(catStr);
                if (hyattCategory < 1 || hyattCategory > 7) {
                    binding.tilHyattCategory.setError("Must be 1–7"); return;
                }
            } catch (NumberFormatException e) {
                binding.tilHyattCategory.setError("Invalid"); return;
            }
        }

        String countStr = binding.etFnCount.getText() != null
                ? binding.etFnCount.getText().toString().trim() : "1";
        int count = 1;
        try { count = Math.max(1, Integer.parseInt(countStr)); }
        catch (NumberFormatException ignored) {}

        String label = binding.etFnLabel.getText() != null
                ? binding.etFnLabel.getText().toString().trim() : "";

        Integer finalPointsCap = pointsCap;
        Integer finalHyattCategory = hyattCategory;
        int finalCount = count;
        Runnable done = () -> requireActivity().runOnUiThread(() ->
                Navigation.findNavController(requireView()).navigateUp());

        if (awardId != -1) {
            viewModel.updateAward(awardId, hotelGroup, limitType,
                    finalPointsCap, finalHyattCategory, label, selectedExpiry.get(), finalCount, done);
        } else {
            viewModel.saveAward(userCardId, hotelGroup, limitType,
                    finalPointsCap, finalHyattCategory, label, selectedExpiry.get(), finalCount, done);
        }
    }

    @Nullable
    private HotelGroup getSelectedHotelGroup() {
        if (binding.chipHotelHilton.isChecked())      return HotelGroup.HILTON;
        if (binding.chipHotelMarriott.isChecked())    return HotelGroup.MARRIOTT;
        if (binding.chipHotelIhg.isChecked())         return HotelGroup.IHG;
        if (binding.chipHotelHyatt.isChecked())       return HotelGroup.HYATT;
        if (binding.chipHotelWyndham.isChecked())     return HotelGroup.WYNDHAM;
        if (binding.chipHotelRadisson.isChecked())    return HotelGroup.RADISSON;
        if (binding.chipHotelChoice.isChecked())      return HotelGroup.CHOICE;
        if (binding.chipHotelBestWestern.isChecked()) return HotelGroup.BEST_WESTERN;
        return null;
    }

    @Nullable
    private FreeNightLimitType getSelectedLimitType() {
        if (binding.chipLimitUnlimited.isChecked())  return FreeNightLimitType.UNLIMITED;
        if (binding.chipLimitPointsCap.isChecked())  return FreeNightLimitType.POINTS_CAP;
        if (binding.chipLimitHyattCat.isChecked())   return FreeNightLimitType.HYATT_CATEGORY;
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
