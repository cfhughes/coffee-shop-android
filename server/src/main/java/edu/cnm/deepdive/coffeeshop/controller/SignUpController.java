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
import edu.cnm.deepdive.coffeeshop.model.dto.SignUpRequest;
import edu.cnm.deepdive.coffeeshop.service.AuthService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/apis/sign-up")
@Profile("service")
public class SignUpController {

  private final AuthService authService;

  public SignUpController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PublicProfile> postProfile(@Valid @RequestBody SignUpRequest request) {
    PublicProfile profile = authService.signUp(request);
    URI location = ServletUriComponentsBuilder
        .fromCurrentContextPath()
        .path("/profiles/{id}")
        .buildAndExpand(profile.id())
        .toUri();
    return ResponseEntity
        .created(location)
        .body(profile);
  }

}
