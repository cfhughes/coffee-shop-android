package edu.cnm.deepdive.coffeeshop.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.coffeeshop.R;
import edu.cnm.deepdive.coffeeshop.databinding.FragmentSettingsBinding;


@AndroidEntryPoint
public class SettingsFragment extends Fragment {

  private static final String PREFS_NAME = "coffee_shop_prefs";
  private static final String KEY_DARK_MODE = "key_dark_mode";
  private static final String KEY_DISTANCE_UNIT = "key_distance_unit";

  private FragmentSettingsBinding binding;
  private SharedPreferences preferences;

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

    preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

    boolean isDarkMode = preferences.getBoolean(KEY_DARK_MODE, false);
    boolean useKilometers = preferences.getBoolean(KEY_DISTANCE_UNIT, false);

    binding.switchDarkMode.setChecked(isDarkMode);
    binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
      preferences.edit()
          .putBoolean("key_dark_mode", isChecked)
          .putBoolean("NAVIGATE_TO_SETTINGS", true)
          .apply();
      if (isChecked) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      }
    });
    binding.switchDistanceUnit.setChecked(useKilometers);
    updateDistanceUnitLabel(useKilometers);
    binding.switchDistanceUnit.setOnCheckedChangeListener((buttonView, isChecked) -> {
      preferences.edit().putBoolean(KEY_DISTANCE_UNIT, isChecked).apply();
      updateDistanceUnitLabel(isChecked);
    });
  }

  private void updateDistanceUnitLabel(boolean useKilometers) {
    int labelResourceId = useKilometers
        ? R.string.distance_unit_kilometers
        : R.string.distance_unit_miles;
    binding.switchDistanceUnit.setText(labelResourceId);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }
}
