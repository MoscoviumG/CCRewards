package com.example.ccrewards;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.ccrewards.databinding.ActivityMainBinding;
import com.example.ccrewards.ui.settings.SettingsFragment;
import com.example.ccrewards.worker.WorkManagerScheduler;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    WorkManagerScheduler.scheduleBenefitReminders(this, prefs);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up Navigation
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            setupBottomNav();
        }

        // Request POST_NOTIFICATIONS permission (Android 13+)
        requestNotificationPermission();
    }

    private void setupBottomNav() {
        // Navigate to a tab root without saving/restoring per-tab back stacks.
        // This ensures tapping a tab always lands on its root destination.
        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                .build();

        binding.bottomNav.setOnItemSelectedListener(item -> {
            try {
                navController.navigate(item.getItemId(), null, navOptions);
            } catch (Exception ignored) {
                // Destination not found — ignore
            }
            return true;
        });

        // Sync the bottom nav highlight whenever the destination changes.
        // We update the menu item's checked state directly (no listener callback).
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            int tabId;
            if (destId == R.id.bestCardFragment
                    || destId == R.id.categoryDetailFragment
                    || destId == R.id.customCategoryDetailFragment) {
                tabId = R.id.bestCardFragment;
            } else if (destId == R.id.creditsFragment
                    || destId == R.id.benefitDetailFragment) {
                tabId = R.id.creditsFragment;
            } else if (destId == R.id.settingsFragment
                    || destId == R.id.pointValuationFragment
                    || destId == R.id.pointCurrencyDetailFragment) {
                tabId = R.id.settingsFragment;
            } else {
                tabId = R.id.myCardsFragment;
            }
            binding.bottomNav.getMenu().findItem(tabId).setChecked(true);
        });
    }

    private void requestNotificationPermission() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enabled = prefs.getBoolean(SettingsFragment.PREF_NOTIFICATIONS_ENABLED, true);
        if (!enabled) return; // User disabled reminders — don't reschedule

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                return;
            }
        }
        WorkManagerScheduler.scheduleBenefitReminders(this, prefs);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }
}
