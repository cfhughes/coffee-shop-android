package edu.cnm.deepdive.coffeeshop.conversion;

import edu.cnm.deepdive.coffeeshop.model.dto.ShopDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Shop;
import java.net.URI;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class ShopToShopDtoConverter implements Converter<Shop, ShopDto> {


  @Override
  public ShopDto convert(Shop source) {
    ShopDto dto = new ShopDto();
    dto.setId(source.getId());
    dto.setName(source.getName());
    dto.setAddress(source.getAddress());
    dto.setPhone(source.getPhone());
    dto.setHours(source.getHours());
    dto.setImageUrl(URI.create(source.getImageUrl()));
    dto.setLat(source.getLat().doubleValue());
    dto.setLng(source.getLng().doubleValue());
    return dto;
  }

}
