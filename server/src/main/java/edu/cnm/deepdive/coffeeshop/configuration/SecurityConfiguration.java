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

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import edu.cnm.deepdive.coffeeshop.service.JwtConverter;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile("service")
public class SecurityConfiguration {

  private static final int SALT_LENGTH = 16;
  private static final int HASH_LENGTH = 32;
  private static final int PARALLELISM = 4;
  private static final int MEMORY = 65536;
  private static final int ITERATIONS = 3;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, JwtConverter converter)
      throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sessions ->
            sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(requests ->
            requests
//                .requestMatchers(HttpMethod.GET, "/shops").anonymous()//Add additional paths as appropriate.
                .requestMatchers(HttpMethod.POST, "/apis/sign-up", "/apis/sign-in", "/apis/sign-out").permitAll()
                .anyRequest().authenticated())
        .oauth2ResourceServer(resourceServer -> resourceServer
            .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)))
        .build();
  }

  @Bean
  JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String encodedSecret) {
    NimbusJwtDecoder decoder = NimbusJwtDecoder
        .withSecretKey(secretKey(encodedSecret))
        .macAlgorithm(MacAlgorithm.HS256)
        .build();
    decoder.setJwtValidator(new JwtTimestampValidator());
    return decoder;
  }

  @Bean
  JwtEncoder jwtEncoder(@Value("${security.jwt.secret}") String encodedSecret) {
    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(encodedSecret)));
  }

  /**
   * Argon2id with the parameters used by the coffee-app backend, so that a hash written by either
   * service can be verified by the other. These values are also what make the encoded hash exactly
   * 97 characters, the width of the {@code profile.password_hash} column.
   */
  @Bean
  PasswordEncoder passwordEncoder() {
    return new Argon2PasswordEncoder(SALT_LENGTH, HASH_LENGTH, PARALLELISM, MEMORY, ITERATIONS);
  }

  private static SecretKey secretKey(String encodedSecret) {
    byte[] secretBytes = Base64.getDecoder().decode(encodedSecret);
    return new SecretKeySpec(secretBytes, "HmacSHA256");
  }
}
