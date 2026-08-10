package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.FavoriteRequestDto;
import edu.cnm.deepdive.coffeeshop.model.dto.ShopDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.model.entity.Shop;
import edu.cnm.deepdive.coffeeshop.repository.ProfileRepository;
import edu.cnm.deepdive.coffeeshop.repository.ShopRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
@org.springframework.context.annotation.Profile("service")
public class FavoriteServiceImpl implements FavoriteService {

  private final ProfileRepository profileRepository;
  private final ShopRepository shopRepository;
  private final Converter<Shop, ShopDto> converter;

  @Autowired
  public FavoriteServiceImpl(ProfileRepository profileRepository,
      ShopRepository shopRepository, Converter<Shop, ShopDto> converter) {
    this.profileRepository = profileRepository;
    this.shopRepository = shopRepository;
    this.converter = converter;
  }

  @Override
  public List<ShopDto> getFavorites(Profile profile) {
    return profile.getFavorites()
        .stream()
        .map(converter::convert)
        .toList();
  }

  @Override
  public ShopDto saveFavorite(FavoriteRequestDto dto, Profile profile) {
    return shopRepository.findById(dto.getShopId())
        .map((shop) -> {
          profileRepository.findById(profile.getId())
              .map((p) -> {
                p.getFavorites().add(shop);
                return profileRepository.save(p);
              });
          return shop;
        })
        .map(converter::convert)
        .orElseThrow();
  }

  @Override
  public void removeFavorite(UUID shopId, Profile profile) {
    shopRepository.findById(shopId)
        .map((shop) -> profileRepository.findById(profile.getId())
            .map((p) -> {
              p.getFavorites().remove(shop);
              return profileRepository.save(p);
            }))
        .orElseThrow();
  }

}
