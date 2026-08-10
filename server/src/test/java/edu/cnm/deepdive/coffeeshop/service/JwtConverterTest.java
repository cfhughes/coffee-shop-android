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

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.repository.ProfileRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class JwtConverterTest {

  @Mock
  private ProfileRepository repository;

  private JwtConverter converter;

  @BeforeEach
  void setUp() {
    converter = new JwtConverter(repository);
  }

  @Test
  void convertsExistingProfile() {
    Profile profile = new Profile();
    when(repository.findByEmail("stable@example.com")).thenReturn(Optional.of(profile));

    var authentication = converter.convert(jwt(Map.of("email", "stable@example.com")));

    assertSame(profile, authentication.getPrincipal());
    verify(repository).findByEmail("stable@example.com");
  }

  @Test
  void rejectsUnknownProfile() {
    when(repository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    assertThrows(OAuth2AuthenticationException.class,
        () -> converter.convert(jwt(Map.of("email", "unknown@example.com"))));
  }

  @Test
  void rejectsMissingEmail() {
    assertThrows(OAuth2AuthenticationException.class,
        () -> converter.convert(jwt(Map.of("sub", "subject"))));
  }

  private static Jwt jwt(Map<String, Object> claims) {
    Instant now = Instant.now();
    return new Jwt("token", now, now.plusSeconds(60), Map.of("alg", "HS256"), claims);
  }
}
