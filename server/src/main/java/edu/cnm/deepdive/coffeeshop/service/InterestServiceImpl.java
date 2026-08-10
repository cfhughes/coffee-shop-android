package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.InterestCreateDto;
import edu.cnm.deepdive.coffeeshop.model.dto.InterestDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Interest;
import edu.cnm.deepdive.coffeeshop.repository.InterestRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
@Profile("service")
public class InterestServiceImpl implements InterestService {

  private final InterestRepository interestRepository;
  private final Converter<Interest, InterestDto> outputConverter;
  private final Converter<InterestCreateDto, Interest> inputConverter;

  @Autowired
  public InterestServiceImpl(InterestRepository interestRepository,
      Converter<Interest, InterestDto> outputConverter,
      Converter<InterestCreateDto, Interest> inputConverter) {
    this.interestRepository = interestRepository;
    this.outputConverter = outputConverter;
    this.inputConverter = inputConverter;
  }

  @Override
  public InterestDto getInterest(UUID interestId) {
    return interestRepository.findById(interestId)
        .map(outputConverter::convert)
        .orElseThrow();
  }

  @Override
  public List<InterestDto> getAllInterests() {
    return interestRepository.getAllByOrderByCategoryAsc()
        .stream()
        .map(outputConverter::convert)
        .toList();
  }

  @Override
  public InterestDto createInterest(InterestCreateDto interest) {
    return outputConverter.convert(interestRepository.save(inputConverter.convert(interest)));
  }

}
