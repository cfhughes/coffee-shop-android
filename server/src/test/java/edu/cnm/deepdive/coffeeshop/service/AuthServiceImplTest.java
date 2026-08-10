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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.coffeeshop.model.dto.PublicProfile;
import edu.cnm.deepdive.coffeeshop.model.dto.SignInRequest;
import edu.cnm.deepdive.coffeeshop.model.dto.SignUpRequest;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.repository.ProfileRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  private static final UUID PROFILE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final String EMAIL = "barista@example.com";
  private static final String PASSWORD = "espresso1";
  private static final String HASH = "$argon2id$v=19$m=65536,t=3,p=4$c2FsdHNhbHRzYWx0c2E$aGFzaA";

  @Mock
  private ProfileRepository repository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private AuthServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new AuthServiceImpl(repository, passwordEncoder);
  }

  @Test
  void signUpStoresEncodedPasswordAndTrimmedName() {
    when(passwordEncoder.encode(PASSWORD)).thenReturn(HASH);
    when(repository.save(any(Profile.class))).thenAnswer((invocation) -> {
      Profile saved = invocation.getArgument(0);
      saved.setId(PROFILE_ID);
      return saved;
    });

    PublicProfile result = service.signUp(
        new SignUpRequest("  Sam Barista  ", EMAIL, PASSWORD, PASSWORD));

    ArgumentCaptor<Profile> captor = ArgumentCaptor.forClass(Profile.class);
    verify(repository).save(captor.capture());
    Profile saved = captor.getValue();
    assertEquals(HASH, saved.getPasswordHash());
    assertEquals("Sam Barista", saved.getName());
    assertEquals(EMAIL, saved.getEmail());
    assertNull(saved.getActivationToken());
    assertEquals(new PublicProfile(PROFILE_ID, "Sam Barista"), result);
  }

  @Test
  void signUpReportsDuplicateEmailAsConflict() {
    when(passwordEncoder.encode(PASSWORD)).thenReturn(HASH);
    when(repository.save(any(Profile.class)))
        .thenThrow(new DataIntegrityViolationException("duplicate key value"));

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.signUp(new SignUpRequest("Sam", EMAIL, PASSWORD, PASSWORD)));

    assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
  }

  @Test
  void authenticateReturnsProfileForCorrectPassword() {
    Profile profile = profile();
    when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(profile));
    when(passwordEncoder.matches(PASSWORD, HASH)).thenReturn(true);

    assertSame(profile, service.authenticate(new SignInRequest(EMAIL, PASSWORD)));
  }

  @Test
  void authenticateRejectsIncorrectPassword() {
    when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(profile()));
    when(passwordEncoder.matches("wrong-password", HASH)).thenReturn(false);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class,
        () -> service.authenticate(new SignInRequest(EMAIL, "wrong-password")));

    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
  }

  @Test
  void authenticateRejectsUnknownEmailIndistinguishablyFromBadPassword() {
    when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(profile()));
    when(passwordEncoder.matches("wrong-password", HASH)).thenReturn(false);
    ResponseStatusException badPassword = assertThrows(ResponseStatusException.class,
        () -> service.authenticate(new SignInRequest(EMAIL, "wrong-password")));

    when(repository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());
    ResponseStatusException unknownEmail = assertThrows(ResponseStatusException.class,
        () -> service.authenticate(new SignInRequest("nobody@example.com", PASSWORD)));

    assertEquals(badPassword.getStatusCode(), unknownEmail.getStatusCode());
    assertEquals(badPassword.getReason(), unknownEmail.getReason());
    verify(passwordEncoder, never()).matches(PASSWORD, null);
  }

  private static Profile profile() {
    Profile profile = new Profile();
    profile.setId(PROFILE_ID);
    profile.setEmail(EMAIL);
    profile.setName("Sam Barista");
    profile.setPasswordHash(HASH);
    return profile;
  }

}
