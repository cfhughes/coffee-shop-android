package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.VisitDto;
import edu.cnm.deepdive.coffeeshop.model.dto.VisitRequestDto;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.model.entity.Visit;
import java.util.List;
import java.util.UUID;

public interface VisitService {

  VisitDto saveVisit(VisitRequestDto visit, UUID profileId);

  VisitDto getVisit(UUID visitId, UUID profileId);

  List<VisitDto> getMyVisits(UUID profileId);
}
