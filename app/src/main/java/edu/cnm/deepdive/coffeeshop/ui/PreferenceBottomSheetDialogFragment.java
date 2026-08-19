package edu.cnm.deepdive.coffeeshop.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import edu.cnm.deepdive.coffeeshop.R;
import java.util.ArrayList;

public class PreferenceBottomSheetDialogFragment extends BottomSheetDialogFragment {

  public static final String REQUEST_KEY = "preference_filter_request";
  public static final String BUNDLE_KEY_SELECTED_PREFS = "selected_preferences";

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    // Inflate your preference dialog layout here (e.g., fragment_settings.xml or a custom preferences' layout)
    return inflater.inflate(R.layout.dialog_preferences, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    // 1. APPLY BUTTON LISTENER
    view.findViewById(R.id.btnApplyPreferences).setOnClickListener(v -> {
      ArrayList<String> selectedPreferences = new ArrayList<>();

      int[] groupIds = {R.id.chipGroupWork, R.id.chipGroupFoodDrink, R.id.chipGroupAmenities};

      for (int groupId : groupIds) {
        ChipGroup group = view.findViewById(groupId);
        if (group != null) {
          for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof Chip chip) {
              if (chip.isChecked()) {
                selectedPreferences.add(chip.getText().toString());
              }
            }
          }
        }
      }

      Bundle result = new Bundle();
      result.putStringArrayList(BUNDLE_KEY_SELECTED_PREFS, selectedPreferences);
      getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);

      dismiss();
    });

    // 2. CLEAR BUTTON LISTENER (ADD THIS RIGHT AFTER LINE 50)
    View clearBtn = view.findViewById(R.id.btnClearFilters);
    if (clearBtn != null) {
      clearBtn.setOnClickListener(v -> {
        int[] groupIds = {R.id.chipGroupWork, R.id.chipGroupFoodDrink, R.id.chipGroupAmenities};

        // Uncheck all chips across groups
        for (int groupId : groupIds) {
          ChipGroup group = view.findViewById(groupId);
          if (group != null) {
            group.clearCheck();
          }
        }

        // Send an empty list back to reset the feed
        Bundle result = new Bundle();
        result.putStringArrayList(BUNDLE_KEY_SELECTED_PREFS, new ArrayList<>());
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, result);

        dismiss();
      });
    }
  }
}
