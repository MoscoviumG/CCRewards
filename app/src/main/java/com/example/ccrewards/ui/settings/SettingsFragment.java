package com.example.ccrewards.ui.settings;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.ccrewards.R;
import com.example.ccrewards.data.ExportImportManager;
import com.example.ccrewards.databinding.FragmentSettingsBinding;
import com.example.ccrewards.worker.BenefitReminderWorker;
import com.example.ccrewards.worker.WorkManagerScheduler;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment {

    public static final String PREF_NOTIFICATIONS_ENABLED = "notifications_enabled";
    public static final String PREF_DARK_MODE = "dark_mode";
    public static final String PREF_LAUNCH_SCREEN = "launch_screen";
    public static final String LAUNCH_MY_CARDS   = "my_cards";
    public static final String LAUNCH_BEST_CARD  = "best_card";
    public static final String LAUNCH_CREDITS    = "credits";
    public static final String LAUNCH_SETTINGS   = "settings";
    /** When true (default), tiles show effective return (8.20%) as primary; false → raw rate (4x UR) as primary. */
    public static final String PREF_BEST_CARD_SHOW_EFFECTIVE = "best_card_show_effective";
    public static final String PREF_NOTIF_DAYS_THRESHOLD = "notification_days_threshold";
    public static final String PREF_NOTIF_TIME_HOUR = "notification_time_hour";
    public static final String PREF_NOTIF_TIME_MINUTE = "notification_time_minute";

    @Inject ExportImportManager exportImportManager;

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;

    private ActivityResultLauncher<String> exportLauncher;
    private ActivityResultLauncher<String[]> importLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exportLauncher = registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/json"),
                uri -> { if (uri != null) doExport(uri); });
        importLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> { if (uri != null) showImportConfirmDialog(uri); });
    }

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

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Portfolio stats
        viewModel.getStats().observe(getViewLifecycleOwner(), stats -> {
            binding.tvStatTotalCards.setText(String.valueOf(stats[0]));
            binding.tvStatAnnualFee.setText("$" + stats[1]);
            binding.tvStat524.setText(stats[2] + " / 5");
        });
        viewModel.getNextDropOff().observe(getViewLifecycleOwner(), date -> {
            if (date != null) {
                binding.tvStat524Next.setText("→ " + date);
                binding.tvStat524Next.setVisibility(View.VISIBLE);
            } else {
                binding.tvStat524Next.setVisibility(View.GONE);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Point valuations row
        binding.rowPointValuations.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_settings_to_pointValuations));

        // Best Card Categories row
        binding.rowManageCategories.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_settings_to_manageCategories));

        // Free Night Valuations row
        binding.rowFreeNightValuations.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_settings_to_freeNightValuations));

        // Theme picker
        updateDarkModeLabel(prefs);
        binding.rowDarkMode.setOnClickListener(v -> showThemeDialog(prefs));

        // Launch screen picker
        updateLaunchScreenLabel(prefs);
        binding.rowLaunchScreen.setOnClickListener(v -> showLaunchScreenDialog(prefs));

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
                BenefitReminderWorker.sendTestNotification(requireContext());
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

        // Export / Import
        binding.rowExportData.setOnClickListener(v ->
                exportLauncher.launch("ccrewards_backup_" + java.time.LocalDate.now() + ".json"));
        binding.rowImportData.setOnClickListener(v ->
                importLauncher.launch(new String[]{"application/json", "*/*"}));
    }

    private void doExport(Uri uri) {
        exportImportManager.exportToUri(uri, new ExportImportManager.Callback() {
            @Override public void onSuccess(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getView() != null)
                        Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT).show();
                });
            }
            @Override public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getView() != null)
                        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showImportConfirmDialog(Uri uri) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Import Data")
                .setMessage("This will replace ALL your existing data and cannot be undone. Continue?")
                .setPositiveButton("Import", (dialog, which) -> doImport(uri))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void doImport(Uri uri) {
        exportImportManager.importFromUri(uri, new ExportImportManager.Callback() {
            @Override public void onSuccess(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getView() == null) return;
                    Snackbar snackbar = Snackbar.make(getView(), msg, Snackbar.LENGTH_SHORT);
                    snackbar.addCallback(new Snackbar.Callback() {
                        @Override public void onDismissed(Snackbar transientBottomBar, int event) {
                            requireActivity().recreate();
                        }
                    });
                    snackbar.show();
                });
            }
            @Override public void onError(String msg) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (getView() != null)
                        Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
                });
            }
        });
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

    private void updateDarkModeLabel(SharedPreferences prefs) {
        int mode = prefs.getInt(PREF_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        String label;
        if (mode == AppCompatDelegate.MODE_NIGHT_YES) label = "Dark";
        else if (mode == AppCompatDelegate.MODE_NIGHT_NO) label = "Light";
        else label = "System";
        binding.tvDarkModeValue.setText(label);
    }

    private void showThemeDialog(SharedPreferences prefs) {
        String[] options = {"System default", "Light", "Dark"};
        int[] modes = {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                AppCompatDelegate.MODE_NIGHT_NO,
                AppCompatDelegate.MODE_NIGHT_YES
        };
        int current = prefs.getInt(PREF_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        int checkedItem = 0;
        for (int i = 0; i < modes.length; i++) {
            if (modes[i] == current) { checkedItem = i; break; }
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Theme")
                .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                    prefs.edit().putInt(PREF_DARK_MODE, modes[which]).apply();
                    AppCompatDelegate.setDefaultNightMode(modes[which]);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateLaunchScreenLabel(SharedPreferences prefs) {
        String key = prefs.getString(PREF_LAUNCH_SCREEN, LAUNCH_MY_CARDS);
        binding.tvLaunchScreenValue.setText(launchScreenLabel(key));
    }

    private void showLaunchScreenDialog(SharedPreferences prefs) {
        String[] labels = {"My Cards", "Best Card", "Credits", "Settings"};
        String[] keys   = {LAUNCH_MY_CARDS, LAUNCH_BEST_CARD, LAUNCH_CREDITS, LAUNCH_SETTINGS};
        String current = prefs.getString(PREF_LAUNCH_SCREEN, LAUNCH_MY_CARDS);
        int checkedItem = 0;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(current)) { checkedItem = i; break; }
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Launch Screen")
                .setSingleChoiceItems(labels, checkedItem, (dialog, which) -> {
                    prefs.edit().putString(PREF_LAUNCH_SCREEN, keys[which]).apply();
                    updateLaunchScreenLabel(prefs);
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static String launchScreenLabel(String key) {
        switch (key) {
            case LAUNCH_BEST_CARD: return "Best Card";
            case LAUNCH_CREDITS:   return "Credits";
            case LAUNCH_SETTINGS:  return "Settings";
            default:               return "My Cards";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
