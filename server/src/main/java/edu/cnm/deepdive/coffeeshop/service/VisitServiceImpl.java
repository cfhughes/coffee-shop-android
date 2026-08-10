package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.VisitDto;
import edu.cnm.deepdive.coffeeshop.model.dto.VisitRequestDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.model.entity.Visit;
import edu.cnm.deepdive.coffeeshop.repository.ProfileRepository;
import edu.cnm.deepdive.coffeeshop.repository.VisitRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
@org.springframework.context.annotation.Profile("service")
public class VisitServiceImpl implements VisitService {

  private final VisitRepository visitRepository;
  private final ProfileRepository profileRepository;
  private final Converter<VisitRequestDto, Visit> inputConverter;
  private final Converter<Visit, VisitDto> outputConverter;

  public VisitServiceImpl(VisitRepository visitRepository, ProfileRepository profileRepository,
      Converter<VisitRequestDto, Visit> inputConverter, Converter<Visit, VisitDto> outputConverter) {
    this.visitRepository = visitRepository;
    this.profileRepository = profileRepository;
    this.inputConverter = inputConverter;
    this.outputConverter = outputConverter;
  }

  @Override
  public VisitDto saveVisit(VisitRequestDto dto, UUID profileId) {
    return profileRepository.findById(profileId)
        .map((profile) -> {
          Visit visit = inputConverter.convert(dto);
          visit.setProfile(profile);
          return visitRepository.save(visit);
        })
        .map(outputConverter::convert)
        .orElseThrow();
  }

  @Override
  public VisitDto getVisit(UUID visitId, UUID profileId) {
    // TODO: 7/31/26 Check to see if access should be restricted to the profile that made the visit.
    return visitRepository.findById(visitId)
        .map(outputConverter::convert)
        .orElseThrow();
  }

  @Override
  @Transactional
  public List<VisitDto> getMyVisits(UUID profileId) {
    return profileRepository.findById(profileId)
        .map(Profile::getVisits)
        .map((visits) -> visits.stream().map(outputConverter::convert).toList())
        .orElseThrow();
  }
}
