package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.InterestCreateDto;
import edu.cnm.deepdive.coffeeshop.model.dto.InterestDto;
import java.util.List;
import java.util.UUID;

public interface InterestService {

  InterestDto getInterest(UUID interestId);

  List<InterestDto> getAllInterests();

  InterestDto createInterest(InterestCreateDto interest);
}
