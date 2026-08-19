package edu.cnm.deepdive.coffeeshop.controller;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.coffeeshop.R;
import edu.cnm.deepdive.coffeeshop.adapter.ShopFeedAdapter;
import edu.cnm.deepdive.coffeeshop.model.domain.Shop;
import edu.cnm.deepdive.coffeeshop.ui.PreferenceBottomSheetDialogFragment;
import edu.cnm.deepdive.coffeeshop.viewmodel.FavoriteViewModel;
import edu.cnm.deepdive.coffeeshop.viewmodel.ShopViewModel;
import edu.cnm.deepdive.coffeeshop.databinding.FragmentShopFeedBinding;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AndroidEntryPoint
public class ShopFeedFragment extends Fragment {

  private static final String PREFERENCES_NAME = "shop_feed_preferences";
  private static final String RATING_PREFIX = "rating_";

  private FragmentShopFeedBinding binding;
  private ShopViewModel shopViewModel;
  private FavoriteViewModel favoriteViewModel;
  private ShopFeedAdapter adapter;
  private List<Shop> currentShops = new ArrayList<>();
  private final Map<UUID, Integer> ratings = new HashMap<>();
  private List<Shop> allShops = new ArrayList<>();

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentShopFeedBinding.inflate(inflater, container, false);
    loadRatings();
    adapter = new ShopFeedAdapter((shop, isFavorite) -> {
      if (isFavorite) {
        favoriteViewModel.addFavorite(shop);
      } else {
        favoriteViewModel.removeFavorite(shop);
      }
      shopViewModel.fetchAllShops();
    }, (shop, rating) -> {
      ratings.put(shop.getId(), rating);
      saveRating(shop.getId(), rating);
      displayShops(currentShops);
    });
    binding.rvShopList.setAdapter(adapter);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    shopViewModel = new ViewModelProvider(this).get(ShopViewModel.class);
    favoriteViewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
    shopViewModel.getShops()
        .observe(getViewLifecycleOwner(), this::displayShops);
    getChildFragmentManager().setFragmentResultListener(
        PreferenceBottomSheetDialogFragment.REQUEST_KEY,
        getViewLifecycleOwner(),
        (requestKey, result) -> {
          List<String> selectedFilters = result.getStringArrayList(
              PreferenceBottomSheetDialogFragment.BUNDLE_KEY_SELECTED_PREFS
          );
          filterShops(selectedFilters);
        }
    );

    // Click listener to open the bottom sheet
    binding.chipPreferences.setOnClickListener(v -> {
      PreferenceBottomSheetDialogFragment dialog = new PreferenceBottomSheetDialogFragment();
      dialog.show(getChildFragmentManager(), "PreferenceDialog");
    });

  }

  private void filterShops(List<String> selectedFilters) {
    if (selectedFilters == null || selectedFilters.isEmpty()) {
      // Show all shops if no filter is selected
      adapter.setShops(allShops);
      return;
    }

    List<Shop> filteredList = new ArrayList<>();

    for (Shop shop : allShops) {
      shop.getPreferences();
      if (!shop.getPreferences().isEmpty()) {
        boolean matchesAll = true;

        for (String filter : selectedFilters) {
          boolean filterFound = false;
          for (String shopPref : shop.getPreferences()) {
            // Flexible match (ignores case or partial label differences)
            if (shopPref.equalsIgnoreCase(filter) || shopPref.toLowerCase().contains(filter.toLowerCase())) {
              filterFound = true;
              break;
            }
          }
          if (!filterFound) {
            matchesAll = false;
            break;
          }
        }

        if (matchesAll) {
          filteredList.add(shop);
        }
      }
    }

    // Update adapter with filtered list
    adapter.setShops(filteredList);
  }

  private void displayShops(List<Shop> shops) {
    if (shops == null) {
      return;
    }

    // 1. Assign preference tags to every shop FIRST
    for (Shop shop : shops) {
      List<String> prefs = new ArrayList<>();
      prefs.add("Oat / Almond Milk"); // Baseline preference for all

      shop.getName();
      if (shop.getName().contains("Amalie")) {
        prefs.add("Work / Study Friendly");
        prefs.add("Outdoor Patio");
        prefs.add("Comfy Seating");
      } else if (shop.getName().contains("Zendo")) {
        prefs.add("Pet Friendly");
        prefs.add("Outdoor Patio");
        prefs.add("Vegan Options");
      } else if (shop.getName().contains("Little Bear")) {
        prefs.add("House-Roasted Beans");
        prefs.add("Strong Wi-Fi");
        prefs.add("Artisan Pour-Over");
      } else {
        prefs.add("Strong Wi-Fi");
        prefs.add("Power Outlets");
      }
      shop.setPreferences(prefs);
    }

    // 2. Save the master list WITH preferences attached
    this.allShops = new ArrayList<>(shops);

    // 3. Sort a copy for initial display
    List<Shop> sortedShops = new ArrayList<>(this.allShops);
    sortedShops.sort(Comparator
        .comparing(Shop::isFavorite).reversed()
        .thenComparing(shop -> ratings.getOrDefault(shop.getId(), 0), Comparator.reverseOrder()));

    // 4. Send updated list to the adapter
    if (ratings != null) {
      adapter.setRatings(ratings);
    }
    adapter.setShops(sortedShops);
  }

  private void loadRatings() {
    SharedPreferences preferences = requireContext().getSharedPreferences(
        PREFERENCES_NAME, 0);
    for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
      if (entry.getKey().startsWith(RATING_PREFIX) && entry.getValue() instanceof Integer) {
        try {
          ratings.put(UUID.fromString(entry.getKey().substring(RATING_PREFIX.length())),
              (Integer) entry.getValue());
        } catch (IllegalArgumentException ignored) {
        }
      }
    }
  }

  private void saveRating(UUID shopId, int rating) {
    requireContext().getSharedPreferences(PREFERENCES_NAME, 0)
        .edit()
        .putInt(RATING_PREFIX + shopId, rating)
        .apply();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  // =========================================================================
  // Inner Adapter Class (nested safely inside ShopFeedFragment)
  // =========================================================================
  private static class QuickShopAdapter extends RecyclerView.Adapter<QuickShopAdapter.ViewHolder> {

    private final List<Map<String, String>> shops;

    public QuickShopAdapter(List<Map<String, String>> shops) {
      this.shops = shops;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.item_shop_card, parent, false);
      return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      Map<String, String> shop = shops.get(position);

      if (holder.tvName != null && shop.containsKey("name")) {
        holder.tvName.setText(shop.get("name"));
      }
      if (holder.tvDescription != null && shop.containsKey("description")) {
        holder.tvDescription.setText(shop.get("description"));

        if (holder.imgShop != null) {
          holder.imgShop.setImageResource(R.drawable.coffee);
        }
      }
    }

    @Override
    public int getItemCount() {
      return shops != null ? shops.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

      TextView tvName;
      TextView tvDescription;
      ImageView imgShop;

      @SuppressLint("WrongViewCast")
      public ViewHolder(@NonNull View itemView) {
        super(itemView);
        // Make sure these IDs match what is inside item_shop_card.xml
        tvName = itemView.findViewById(R.id.ivShopImage);
        tvDescription = itemView.findViewById(R.id.tvShopDescription);
        imgShop = itemView.findViewById(R.id.image_shop);
      }
    }

  }


}
