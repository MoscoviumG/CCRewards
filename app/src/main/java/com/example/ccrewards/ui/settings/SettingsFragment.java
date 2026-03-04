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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    public static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    /** When true (default), tiles show effective return (8.20%) as primary; false → raw rate (4x UR) as primary. */
    public static final String PREF_BEST_CARD_SHOW_EFFECTIVE = "best_card_show_effective";
    public static final String PREF_NOTIF_DAYS_THRESHOLD = "notification_days_threshold";
    public static final String PREF_NOTIF_TIME_HOUR = "notification_time_hour";
    public static final String PREF_NOTIF_TIME_MINUTE = "notification_time_minute";

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

        // Best Card display mode toggle
        boolean showEffective = prefs.getBoolean(PREF_BEST_CARD_SHOW_EFFECTIVE, true);
        binding.switchBestCardDisplay.setChecked(showEffective);
        binding.switchBestCardDisplay.setOnCheckedChangeListener((btn, checked) ->
                prefs.edit().putBoolean(PREF_BEST_CARD_SHOW_EFFECTIVE, checked).apply());

        // Notification toggle
        boolean notifEnabled = prefs.getBoolean(PREF_NOTIFICATIONS_ENABLED, true);
        binding.switchNotifications.setChecked(notifEnabled);
        binding.switchNotifications.setOnCheckedChangeListener((btn, checked) -> {
            prefs.edit().putBoolean(PREF_NOTIFICATIONS_ENABLED, checked).apply();
            if (checked) {
                WorkManagerScheduler.scheduleBenefitReminders(requireContext(), prefs);
            } else {
                WorkManagerScheduler.cancelBenefitReminders(requireContext());
            }
        });

        // Days before expiry
        updateDaysLabel(prefs);
        binding.rowNotificationDays.setOnClickListener(v -> showDaysDialog(prefs));

        // Reminder time
        updateTimeLabel(prefs);
        binding.rowNotificationTime.setOnClickListener(v -> showTimePicker(prefs));
    }

    private void updateDaysLabel(SharedPreferences prefs) {
        int days = prefs.getInt(PREF_NOTIF_DAYS_THRESHOLD, 7);
        binding.tvNotificationDaysValue.setText(days + " days");
    }

    private void updateTimeLabel(SharedPreferences prefs) {
        int hour = prefs.getInt(PREF_NOTIF_TIME_HOUR, 9);
        int minute = prefs.getInt(PREF_NOTIF_TIME_MINUTE, 0);
        String amPm = hour < 12 ? "AM" : "PM";
        int displayHour = hour % 12 == 0 ? 12 : hour % 12;
        binding.tvNotificationTimeValue.setText(
                String.format(java.util.Locale.US, "%d:%02d %s", displayHour, minute, amPm));
    }

    private void showDaysDialog(SharedPreferences prefs) {
        String[] options = {"1 day", "3 days", "5 days", "7 days", "14 days", "30 days"};
        int[] values = {1, 3, 5, 7, 14, 30};
        int current = prefs.getInt(PREF_NOTIF_DAYS_THRESHOLD, 7);
        int checkedItem = 3; // default index for 7
        for (int i = 0; i < values.length; i++) {
            if (values[i] == current) { checkedItem = i; break; }
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Days before expiry")
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    prefs.edit().putInt(PREF_NOTIF_DAYS_THRESHOLD, values[which]).apply();
                    updateDaysLabel(prefs);
                    WorkManagerScheduler.scheduleBenefitReminders(requireContext(), prefs);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showTimePicker(SharedPreferences prefs) {
        int hour = prefs.getInt(PREF_NOTIF_TIME_HOUR, 9);
        int minute = prefs.getInt(PREF_NOTIF_TIME_MINUTE, 0);
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Reminder time")
                .build();
        picker.addOnPositiveButtonClickListener(v -> {
            prefs.edit()
                    .putInt(PREF_NOTIF_TIME_HOUR, picker.getHour())
                    .putInt(PREF_NOTIF_TIME_MINUTE, picker.getMinute())
                    .apply();
            updateTimeLabel(prefs);
            WorkManagerScheduler.scheduleBenefitReminders(requireContext(), prefs);
        });
        picker.show(getParentFragmentManager(), "time_picker");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
