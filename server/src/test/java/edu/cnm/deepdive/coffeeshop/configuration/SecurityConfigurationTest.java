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
package edu.cnm.deepdive.coffeeshop.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.time.Instant;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

class SecurityConfigurationTest {

  private static final byte[] SECRET = new byte[32];
  private static final byte[] OTHER_SECRET = new byte[32];

  static {
    OTHER_SECRET[0] = 1;
  }

  private final SecurityConfiguration configuration = new SecurityConfiguration();

  @Test
  void acceptsValidHs256Token() {
    String token = token(SECRET, Instant.now().plusSeconds(60));

    assertEquals("stable-name", decoder(SECRET).decode(token).getClaimAsString("name"));
  }

  @Test
  void rejectsExpiredToken() {
    String token = token(SECRET, Instant.now().minusSeconds(60));

    assertThrows(JwtException.class, () -> decoder(SECRET).decode(token));
  }

  @Test
  void rejectsWrongSignature() {
    String token = token(OTHER_SECRET, Instant.now().plusSeconds(60));

    assertThrows(JwtException.class, () -> decoder(SECRET).decode(token));
  }

  @Test
  void encodesPasswordsToFitThePasswordHashColumn() {
    var encoder = configuration.passwordEncoder();

    String hash = encoder.encode("espresso1");

    assertEquals(97, hash.length());
    assertTrue(hash.startsWith("$argon2id$v=19$m=65536,t=3,p=4$"));
    assertTrue(encoder.matches("espresso1", hash));
    assertFalse(encoder.matches("espresso2", hash));
  }

  private org.springframework.security.oauth2.jwt.JwtDecoder decoder(byte[] secret) {
    return configuration.jwtDecoder(Base64.getEncoder().encodeToString(secret));
  }

  private static String token(byte[] secret, Instant expiresAt) {
    Instant now = Instant.now();
    var claims = JwtClaimsSet.builder()
        .subject("subject")
        .claim("name", "stable-name")
        .issuedAt(now.minusSeconds(120))
        .expiresAt(expiresAt)
        .build();
    var header = JwsHeader.with(MacAlgorithm.HS256).build();
    var encoder = new NimbusJwtEncoder(new ImmutableSecret<>(secret));
    return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
  }
}
