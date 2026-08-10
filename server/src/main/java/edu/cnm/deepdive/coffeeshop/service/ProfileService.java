package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.ProfileUpdateRequestDto;
import edu.cnm.deepdive.coffeeshop.model.dto.PublicProfileDto;
import java.util.UUID;

public interface ProfileService {

  PublicProfileDto getProfile(UUID profileId);

  PublicProfileDto updateProfile(UUID profileId, ProfileUpdateRequestDto Update);

}
