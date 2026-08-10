package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.FavoriteRequestDto;
import edu.cnm.deepdive.coffeeshop.model.dto.ShopDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.model.entity.Shop;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface FavoriteService {

  List<ShopDto> getFavorites(Profile profile);
  // TODO: 7/30/26 Verify returning shops is okay.

  ShopDto saveFavorite(FavoriteRequestDto dto, Profile profile);
  // TODO: 7/30/26 Verify that returning nothing is okay.

  void removeFavorite(UUID  shopId, Profile profile);
  // TODO: 7/30/26 Verify that returning nothing is okay.
}
