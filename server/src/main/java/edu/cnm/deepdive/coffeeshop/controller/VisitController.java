package edu.cnm.deepdive.coffeeshop.controller;

import edu.cnm.deepdive.coffeeshop.controller.api.VisitApi;
import edu.cnm.deepdive.coffeeshop.model.dto.VisitDto;
import edu.cnm.deepdive.coffeeshop.model.dto.VisitRequestDto;
import edu.cnm.deepdive.coffeeshop.service.ContextProfileService;
import edu.cnm.deepdive.coffeeshop.service.VisitService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("service")
public class VisitController implements VisitApi {

  private final VisitService visitService;
  private final ContextProfileService contextProfileService;

  public VisitController(VisitService visitService, ContextProfileService contextProfileService) {
    this.visitService = visitService;
    this.contextProfileService = contextProfileService;
  }

  @Override
  public ResponseEntity<VisitDto> createVisit(VisitRequestDto visitRequestDto) {
    VisitDto dto = visitService.saveVisit(visitRequestDto,
        contextProfileService.getContextProfile().getId());
    URI location = WebMvcLinkBuilder.linkTo(
        WebMvcLinkBuilder.methodOn(getClass()).getVisitById(dto.getId())).toUri();
    return ResponseEntity.created(location).body(dto);
  }

  @Override
  public ResponseEntity<VisitDto> getVisitById(UUID id) {
    return ResponseEntity.ok(
        visitService.getVisit(id, contextProfileService.getContextProfile().getId()));
  }

  @Override
  public ResponseEntity<List<VisitDto>> listMyVisits() {
    return ResponseEntity.ok(
        visitService.getMyVisits(contextProfileService.getContextProfile().getId()));
  }

}
