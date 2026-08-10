package edu.cnm.deepdive.coffeeshop.conversion;

import edu.cnm.deepdive.coffeeshop.model.dto.InterestDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Interest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InterestToInterestDtoConverter implements Converter<Interest, InterestDto> {

  @Override
  public InterestDto convert(Interest source) {
    InterestDto dto = new InterestDto();
    // Assuming these are the fields your DTO has:
    dto.setId(source.getId());
    dto.setCategory(source.getCategory());
    return dto;
  }
}
