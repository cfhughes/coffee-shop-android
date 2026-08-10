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
package edu.cnm.deepdive.coffeeshop.controller;

import edu.cnm.deepdive.coffeeshop.model.dto.PublicProfile;
import edu.cnm.deepdive.coffeeshop.model.dto.SignInRequest;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.service.JwtIssuer;
import edu.cnm.deepdive.coffeeshop.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apis/sign-in")
@org.springframework.context.annotation.Profile("service")
public class SignInController {

  private static final String BEARER_PREFIX = "Bearer ";

  private final AuthService authService;
  private final JwtIssuer jwtIssuer;

  public SignInController(AuthService authService, JwtIssuer jwtIssuer) {
    this.authService = authService;
    this.jwtIssuer = jwtIssuer;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PublicProfile> postCredentials(@Valid @RequestBody SignInRequest request) {
    Profile profile = authService.authenticate(request);
    return ResponseEntity
        .ok()
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + jwtIssuer.issue(profile))
        .body(PublicProfile.of(profile));
  }

}
