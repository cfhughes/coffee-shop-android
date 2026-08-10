package edu.cnm.deepdive.coffeeshop.controller;

import edu.cnm.deepdive.coffeeshop.controller.api.InterestApi;
import edu.cnm.deepdive.coffeeshop.model.dto.InterestCreateDto;
import edu.cnm.deepdive.coffeeshop.model.dto.InterestDto;
import edu.cnm.deepdive.coffeeshop.service.InterestService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Profile("service")
public class InterestController implements InterestApi {

  private final InterestService interestService;

  public InterestController(InterestService interestService) {
    this.interestService = interestService;
  }

  @Override
  public ResponseEntity<InterestDto> getInterestById(UUID id) {
    InterestDto dto = interestService.getInterest(id);
    return ResponseEntity.ok(dto);
  }

  @Override
  public ResponseEntity<List<InterestDto>> listInterests() {
    List<InterestDto> interests = interestService.getAllInterests();
    return ResponseEntity.ok(interests);
  }

  @PostMapping(path = InterestApi.PATH_LIST_INTERESTS, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InterestDto> createInterest(@Valid @RequestBody InterestCreateDto interest) {
    InterestDto dto = interestService.createInterest(interest);
    URI location = WebMvcLinkBuilder.linkTo(
        WebMvcLinkBuilder.methodOn(InterestController.class).getInterestById(dto.getId())).toUri();
    return ResponseEntity.created(location).body(dto);
  }

}
