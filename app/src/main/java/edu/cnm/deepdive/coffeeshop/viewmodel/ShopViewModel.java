package edu.cnm.deepdive.coffeeshop.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.coffeeshop.R;
import edu.cnm.deepdive.coffeeshop.model.domain.Shop;
import edu.cnm.deepdive.coffeeshop.service.ShopService;
import edu.cnm.deepdive.coffeeshop.service.VisitService;
import java.util.List;
import java.util.UUID;
import jakarta.inject.Inject;

@HiltViewModel
public class ShopViewModel extends ViewModel {

  private final ShopService shopService;
  private final MutableLiveData<List<Shop>> shops;

  @Inject
  public ShopViewModel (ShopService shopService, VisitService visitService) {
    this.shopService = shopService;
    shops = new MutableLiveData<>();
    fetchAllShops();
  }

  public LiveData<List<Shop>> getShops() {
    return shops;
  }

  public void fetchAllShops() {
    shopService.getShops()
        .thenAccept(shops::postValue);
  }

  private static List<Shop> buildTestShops() {
    return List.of(
        new Shop(
            UUID.randomUUID(),
            "Average coffee shop",
            null, null, null, null, null, null,
            true,
            List.of("Work / Study Friendly", "Strong Wi-Fi", "Outdoor Patio") // <-- Add 10th parameter here
        ),
        new Shop(
            UUID.randomUUID(),
            "Espresso Express",
            null, null, null, null, null, null,
            false,
            List.of("Drive-Thru", "Oat / Almond Milk")
        ),
        new Shop(
            UUID.randomUUID(),
            "Bean & Brew",
            null, null, null, null, null, null,
            false,
            List.of("House-Roasted Beans", "Power Outlets", "Comfy Seating")
        ),
        new Shop(
            UUID.randomUUID(),
            "Roast & Co.",
            null, null, null, null, null, null,
            false,
            List.of("House-Roasted Beans", "Artisan Pour-Over", "Quiet Environment")
        ),
        new Shop(
            UUID.randomUUID(),
            "The Daily Grind",
            null, null, null, null, null, null,
            true,
            List.of("Work / Study Friendly", "Strong Wi-Fi", "Power Outlets")
        )
    );
  }

  public void setFavorite(Shop shop, boolean isFavorite) {

  }
}
