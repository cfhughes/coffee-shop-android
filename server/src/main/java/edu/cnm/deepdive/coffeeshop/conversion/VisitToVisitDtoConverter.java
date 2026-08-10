package edu.cnm.deepdive.coffeeshop.conversion;

import edu.cnm.deepdive.coffeeshop.model.dto.VisitDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Visit;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class VisitToVisitDtoConverter implements Converter<Visit, VisitDto> {

  @Override
  public VisitDto convert(Visit source) {
    VisitDto visitDto = new VisitDto();
    visitDto.setId(source.getId());
    visitDto.setShopId(source.getShop().getId());
    visitDto.setProfileId(source.getProfile().getId());
    visitDto.setCreatedAt(source.getCreatedAt());
    return visitDto;
  }

}
