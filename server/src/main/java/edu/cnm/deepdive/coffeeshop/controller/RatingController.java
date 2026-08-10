package edu.cnm.deepdive.coffeeshop.controller;

import edu.cnm.deepdive.coffeeshop.controller.api.RatingApi;
import edu.cnm.deepdive.coffeeshop.model.dto.RatingDto;
import edu.cnm.deepdive.coffeeshop.model.dto.RatingRequestDto;
import edu.cnm.deepdive.coffeeshop.model.dto.RatingUpdateRequestDto;
import edu.cnm.deepdive.coffeeshop.service.RatingService;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("service")
public class RatingController implements RatingApi {
  
  private final RatingService ratingService;

  public RatingController(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  @Override
  public ResponseEntity<RatingDto> createRating(UUID visitId, RatingRequestDto ratingRequestDto) {
    return null;
  }

  @Override
  public ResponseEntity<Void> deleteRating(UUID visitId, UUID interestId) {
    return null;
  }

  @Override
  public ResponseEntity<List<RatingDto>> listRatings(UUID visitId) {
    return null;
  }

  @Override
  public ResponseEntity<RatingDto> updateRating(UUID visitId, UUID interestId,
      RatingUpdateRequestDto ratingUpdateRequestDto) {
    return null;
  }
}
