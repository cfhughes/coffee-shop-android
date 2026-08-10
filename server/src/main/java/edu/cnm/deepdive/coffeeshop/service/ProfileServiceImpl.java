package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.ProfileUpdateRequestDto;
import edu.cnm.deepdive.coffeeshop.model.dto.PublicProfileDto;
import edu.cnm.deepdive.coffeeshop.repository.ProfileRepository;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("service")
public class ProfileServiceImpl implements ProfileService {

  private final ProfileRepository profileRepository;

  public ProfileServiceImpl(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  @Override
  public PublicProfileDto getProfile(UUID profileId) {
    return profileRepository.findById(profileId)
        .map((profile) -> {
          PublicProfileDto dto = new PublicProfileDto();
          dto.setName(profile.getName());
          dto.setId(profile.getId());
          return dto;
        })
        .orElseThrow();
  }

  @Override
  public PublicProfileDto updateProfile(UUID profileId, ProfileUpdateRequestDto update) {
    return profileRepository.findById(profileId)
        .map((profile) -> {
          profile.setName(update.getName());
          profile = profileRepository.save(profile);
          PublicProfileDto dto = new PublicProfileDto();
          dto.setName(profile.getName());
          dto.setId(profile.getId());
          return dto;
        })
        .orElseThrow();
  }
}
