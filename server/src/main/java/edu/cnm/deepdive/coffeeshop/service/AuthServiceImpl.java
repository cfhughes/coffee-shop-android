/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.coffeeshop.service;

import edu.cnm.deepdive.coffeeshop.model.dto.PublicProfile;
import edu.cnm.deepdive.coffeeshop.model.dto.SignInRequest;
import edu.cnm.deepdive.coffeeshop.model.dto.SignUpRequest;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@org.springframework.context.annotation.Profile("service")
public class AuthServiceImpl implements AuthService {

  private static final String DUPLICATE_MESSAGE =
      "An account with that email or name already exists";
  private static final String INVALID_CREDENTIALS_MESSAGE =
      "Email or password is incorrect please try again";

  private final ProfileRepository repository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public AuthServiceImpl(ProfileRepository repository, PasswordEncoder passwordEncoder) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public PublicProfile signUp(SignUpRequest request) {
    Profile profile = new Profile();
    profile.setName(request.name().trim());
    profile.setEmail(request.email());
    profile.setPasswordHash(passwordEncoder.encode(request.password()));
    try {
      return PublicProfile.of(repository.save(profile));
    } catch (DataIntegrityViolationException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, DUPLICATE_MESSAGE, e);
    }
  }

  @Override
  public Profile authenticate(SignInRequest request) {
    return repository
        .findByEmail(request.email())
        .filter((profile) -> passwordEncoder.matches(request.password(), profile.getPasswordHash()))
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_CREDENTIALS_MESSAGE));
  }

}
