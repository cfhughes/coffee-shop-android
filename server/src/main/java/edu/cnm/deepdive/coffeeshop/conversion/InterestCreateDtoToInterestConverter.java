package edu.cnm.deepdive.coffeeshop.conversion;

import edu.cnm.deepdive.coffeeshop.model.dto.InterestCreateDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Interest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class InterestCreateDtoToInterestConverter implements
    Converter<InterestCreateDto, Interest> {

  @Override
  public Interest convert(InterestCreateDto source) {
    Interest interest = new Interest();
    interest.setCategory(source.category());
    return interest;
  }

}
