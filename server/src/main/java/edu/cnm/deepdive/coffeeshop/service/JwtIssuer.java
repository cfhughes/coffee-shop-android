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

import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@org.springframework.context.annotation.Profile("service")
public class JwtIssuer {

  private final JwtEncoder encoder;
  private final Duration timeToLive;

  public JwtIssuer(JwtEncoder encoder, @Value("${security.jwt.ttl}") Duration timeToLive) {
    this.encoder = encoder;
    this.timeToLive = timeToLive;
  }

  public String issue(Profile profile) {
    Instant issuedAt = Instant.now();
    JwtClaimsSet claims = JwtClaimsSet
        .builder()
        .subject(profile.getId().toString())
        .claim("email", profile.getEmail())
        .claim("name", profile.getName())
        .issuedAt(issuedAt)
        .expiresAt(issuedAt.plus(timeToLive))
        .build();
    JwsHeader header = JwsHeader
        .with(MacAlgorithm.HS256)
        .build();
    return encoder
        .encode(JwtEncoderParameters.from(header, claims))
        .getTokenValue();
  }

}
