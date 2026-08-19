package edu.cnm.deepdive.coffeeshop.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.coffeeshop.R;
import edu.cnm.deepdive.coffeeshop.databinding.ActivityMainBinding;
import edu.cnm.deepdive.coffeeshop.viewmodel.AuthViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;
  private NavController navController;
  private AuthViewModel authViewModel;
  private boolean isAuthenticatedDestination;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    SharedPreferences prefs = getSharedPreferences("coffee_shop_prefs", MODE_PRIVATE);
    boolean isDarkMode = prefs.getBoolean("key_dark_mode", false);

    if (isDarkMode) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    } else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    super.onCreate(savedInstanceState);

    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
        .findFragmentById(R.id.nav_host_fragment);

    if (navHostFragment != null) {
      navController = navHostFragment.getNavController();
      NavigationUI.setupWithNavController(binding.bottomNav, navController);
      boolean navigateToSettings = prefs.getBoolean("NAVIGATE_TO_SETTINGS", false);

      if (navigateToSettings) {
        // Clear flag so it only redirects once
        prefs.edit().putBoolean("NAVIGATE_TO_SETTINGS", false).apply();

        // Navigate directly back to Settings Fragment
        navController.navigate(R.id.settingsFragment);
      }
      // ----------------------------

      navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
        // ... existing destination changed listener code ...
      });
      navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
        int destinationId = destination.getId();
        isAuthenticatedDestination = destinationId == R.id.loggedInFragment
            || destinationId == R.id.shopFeedFragment
            || destinationId == R.id.profilePageFragment
            || destinationId == R.id.settingsFragment;
        binding.bottomNav.setVisibility(isAuthenticatedDestination ? View.VISIBLE : View.GONE);
        invalidateOptionsMenu();
      });
      authViewModel.getSignedInProfile().observe(this, (profile) -> {
        if (profile == null) {
          navigateToSignIn();
        }
      });
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main_app_options, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.findItem(R.id.settings).setVisible(isAuthenticatedDestination);
    menu.findItem(R.id.sign_out).setVisible(isAuthenticatedDestination);
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();
    if (itemId == R.id.settings) {
      navController.navigate(R.id.settingsFragment);
      return true;
    }
    if (itemId == R.id.sign_out) {
      authViewModel.signOut();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void navigateToSignIn() {
    if (navController == null || navController.getCurrentDestination() == null
        || navController.getCurrentDestination().getId() == R.id.signInFragment) {
      return;
    }
    NavOptions options = new NavOptions.Builder()
        .setPopUpTo(R.id.nav_graph, true)
        .build();
    navController.navigate(R.id.signInFragment, null, options);
  }
}
