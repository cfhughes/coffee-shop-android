package edu.cnm.deepdive.coffeeshop.controller;

import edu.cnm.deepdive.coffeeshop.controller.api.ProfileApi;
import edu.cnm.deepdive.coffeeshop.model.dto.ProfileUpdateRequestDto;
import edu.cnm.deepdive.coffeeshop.model.dto.PublicProfileDto;
import edu.cnm.deepdive.coffeeshop.service.ContextProfileService;
import edu.cnm.deepdive.coffeeshop.service.ProfileService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("service")
public class ProfileController implements ProfileApi {

  private final ProfileService profileService;
  private final ContextProfileService contextProfileService;

  public ProfileController(ProfileService profileService,
      ContextProfileService contextProfileService) {
    this.profileService = profileService;
    this.contextProfileService = contextProfileService;
  }

  @Override
  public ResponseEntity<PublicProfileDto> getMyProfile() {
    return ResponseEntity.ok(profileService.getProfile(contextProfileService.getContextProfile().getId()));
  }

  @Override
  public ResponseEntity<PublicProfileDto> updateMyProfile(
      ProfileUpdateRequestDto profileUpdateRequestDto) {
    return ResponseEntity.ok(
        profileService.updateProfile(contextProfileService.getContextProfile().getId(), profileUpdateRequestDto));
  }

}
