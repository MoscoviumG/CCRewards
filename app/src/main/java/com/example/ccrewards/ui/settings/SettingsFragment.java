package com.example.ccrewards.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ccrewards.R;
import com.example.ccrewards.databinding.FragmentSettingsBinding;
import com.example.ccrewards.worker.WorkManagerScheduler;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    public static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";

    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Point valuations row
        binding.rowPointValuations.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_settings_to_pointValuations));

        // Notification toggle
        boolean notifEnabled = prefs.getBoolean(PREF_NOTIFICATIONS_ENABLED, true);
        binding.switchNotifications.setChecked(notifEnabled);
        binding.switchNotifications.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean(PREF_NOTIFICATIONS_ENABLED, checked).apply();
            if (checked) {
                WorkManagerScheduler.scheduleBenefitReminders(requireContext());
            } else {
                WorkManagerScheduler.cancelBenefitReminders(requireContext());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
