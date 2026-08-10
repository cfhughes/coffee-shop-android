package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.conversion.ShopToShopDtoConverter;
import edu.cnm.deepdive.coffeeshop.model.dto.ShopDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.model.entity.Shop;
import edu.cnm.deepdive.coffeeshop.repository.ShopRepository;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
@org.springframework.context.annotation.Profile("service")
public class ShopServiceImpl implements ShopService {

  private final ShopRepository repository;
  private final Converter<Shop, ShopDto> converter;

  @Autowired
  public ShopServiceImpl(ShopRepository repository, Converter<Shop, ShopDto> converter) {
    this.repository = repository;
    this.converter = converter;
  }

  @Override
  public ShopDto getShop(UUID id) {
    return repository.findById(id)
        .map(converter::convert)
        .orElseThrow();
  }

  @Override
  public ShopDto saveShop(Shop shop, Profile profile) {
    return converter.convert(repository.save(shop));
  }

  @Override
  public List<ShopDto> getAllShops() {
    return repository.findAll().stream()
        .map(converter::convert)
        .toList();
  }

}
