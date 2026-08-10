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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.repository.ProfileRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class JwtIssuerTest {

  private static final byte[] SECRET = new byte[32];
  private static final UUID PROFILE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final String EMAIL = "barista@example.com";

  @Test
  void issuesTokenCarryingTheClaimsTheDecoderExpects() {
    String token = new JwtIssuer(encoder(), Duration.ofHours(3)).issue(profile());

    Jwt decoded = decode(token);

    assertEquals(PROFILE_ID.toString(), decoded.getSubject());
    assertEquals(EMAIL, decoded.getClaimAsString("email"));
    assertEquals("Sam Barista", decoded.getClaimAsString("name"));
    assertTrue(decoded.getExpiresAt().isAfter(Instant.now()));
  }

  @Test
  void issuedTokenResolvesBackToItsProfileThroughJwtConverter() {
    Profile profile = profile();
    ProfileRepository repository = mock(ProfileRepository.class);
    when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(profile));
    String token = new JwtIssuer(encoder(), Duration.ofHours(3)).issue(profile);

    var authentication = new JwtConverter(repository).convert(decode(token));

    assertSame(profile, authentication.getPrincipal());
  }

  @Test
  void honorsConfiguredTimeToLive() {
    String token = new JwtIssuer(encoder(), Duration.ofMinutes(5)).issue(profile());

    Jwt decoded = decode(token);

    assertEquals(Duration.ofMinutes(5),
        Duration.between(decoded.getIssuedAt(), decoded.getExpiresAt()));
  }

  private static NimbusJwtEncoder encoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(SECRET));
  }

  /**
   * Decodes with the same algorithm and key material as the {@code jwtDecoder} bean in
   * {@link edu.cnm.deepdive.coffeeshop.configuration.SecurityConfiguration}, which is verified
   * separately by {@code SecurityConfigurationTest}.
   */
  private static Jwt decode(String token) {
    return NimbusJwtDecoder
        .withSecretKey(new SecretKeySpec(SECRET, "HmacSHA256"))
        .macAlgorithm(MacAlgorithm.HS256)
        .build()
        .decode(token);
  }

  private static Profile profile() {
    Profile profile = new Profile();
    profile.setId(PROFILE_ID);
    profile.setEmail(EMAIL);
    profile.setName("Sam Barista");
    return profile;
  }

}
