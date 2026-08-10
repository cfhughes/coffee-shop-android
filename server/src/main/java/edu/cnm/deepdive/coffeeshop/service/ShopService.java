package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.ShopDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.model.entity.Shop;
import java.util.List;
import java.util.UUID;

public interface ShopService {

  ShopDto getShop(UUID id);

  ShopDto saveShop(Shop shop, Profile profile);

  List<ShopDto> getAllShops();

}
