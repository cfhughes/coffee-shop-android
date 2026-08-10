package edu.cnm.deepdive.coffeeshop.conversion;

import edu.cnm.deepdive.coffeeshop.model.dto.VisitRequestDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Visit;
import edu.cnm.deepdive.coffeeshop.repository.ShopRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class VisitRequestDtoToVisitConverter implements Converter<VisitRequestDto, Visit> {

  private final ShopRepository shopRepository;

  public VisitRequestDtoToVisitConverter(ShopRepository shopRepository) {
    this.shopRepository = shopRepository;
  }

  @Override
  public Visit convert(VisitRequestDto source) {
    return shopRepository.findById(source.getShopId())
        .map((shop) -> {
          Visit visit = new Visit();
          visit.setShop(shop);
          return visit;
        })
        .orElseThrow();
  }

}
