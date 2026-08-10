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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.cnm.deepdive.coffeeshop.configuration.SecurityConfiguration;
import edu.cnm.deepdive.coffeeshop.model.dto.PublicProfile;
import edu.cnm.deepdive.coffeeshop.model.dto.SignInRequest;
import edu.cnm.deepdive.coffeeshop.model.dto.SignUpRequest;
import edu.cnm.deepdive.coffeeshop.model.entity.Profile;
import edu.cnm.deepdive.coffeeshop.service.JwtConverter;
import edu.cnm.deepdive.coffeeshop.service.JwtIssuer;
import edu.cnm.deepdive.coffeeshop.service.AuthService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest({SignUpController.class, SignInController.class, SignOutController.class})
@Import(SecurityConfiguration.class)
class AuthenticationControllerTest {

  private static final UUID PROFILE_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final String EMAIL = "barista@example.com";
  private static final String PASSWORD = "espresso1";
  private static final String TOKEN = "issued.jwt.value";

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private AuthService authService;

  @MockitoBean
  private JwtIssuer jwtIssuer;

  @MockitoBean
  private JwtConverter jwtConverter;

  @Test
  void signUpReturnsCreatedProfile() throws Exception {
    when(authService.signUp(any(SignUpRequest.class)))
        .thenReturn(new PublicProfile(PROFILE_ID, "Sam Barista"));

    mockMvc
        .perform(post("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name": "Sam Barista", "email": "%s", "password": "%s", "passwordConfirm": "%s"}
                """.formatted(EMAIL, PASSWORD, PASSWORD)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/profiles/" + PROFILE_ID))
        .andExpect(jsonPath("$.id").value(PROFILE_ID.toString()))
        .andExpect(jsonPath("$.name").value("Sam Barista"));
  }

  @Test
  void signUpRejectsMismatchedPasswordConfirmation() throws Exception {
    mockMvc
        .perform(post("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name": "Sam", "email": "%s", "password": "%s", "passwordConfirm": "different"}
                """.formatted(EMAIL, PASSWORD)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.path").value("/sign-up"))
        .andExpect(jsonPath("$.message").value("passwords do not match"))
        .andExpect(jsonPath("$.details.issues[0].path").value("passwordConfirmed"));
  }

  @Test
  void signUpReportsDuplicateEmailAsConflict() throws Exception {
    when(authService.signUp(any(SignUpRequest.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT,
            "An account with that email or name already exists",
            new DataIntegrityViolationException("duplicate key value")));

    mockMvc
        .perform(post("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"name": "Sam", "email": "%s", "password": "%s", "passwordConfirm": "%s"}
                """.formatted(EMAIL, PASSWORD, PASSWORD)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message")
            .value("An account with that email or name already exists"));
  }

  @Test
  void signInReturnsProfileAndBearerToken() throws Exception {
    when(authService.authenticate(any(SignInRequest.class))).thenReturn(profile());
    when(jwtIssuer.issue(any(Profile.class))).thenReturn(TOKEN);

    mockMvc
        .perform(post("/sign-in")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email": "%s", "password": "%s"}
                """.formatted(EMAIL, PASSWORD)))
        .andExpect(status().isOk())
        .andExpect(header().string("Authorization", "Bearer " + TOKEN))
        .andExpect(jsonPath("$.id").value(PROFILE_ID.toString()))
        .andExpect(jsonPath("$.name").value("Sam Barista"))
        .andExpect(jsonPath("$.email").doesNotExist())
        .andExpect(jsonPath("$.passwordHash").doesNotExist());
  }

  @Test
  void signInRejectsBadCredentialsWithoutIssuingAToken() throws Exception {
    when(authService.authenticate(any(SignInRequest.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
            "Email or password is incorrect please try again"));

    mockMvc
        .perform(post("/sign-in")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email": "%s", "password": "wrong-password"}
                """.formatted(EMAIL)))
        .andExpect(status().isUnauthorized())
        .andExpect(header().doesNotExist("Authorization"))
        .andExpect(jsonPath("$.status").value(401))
        .andExpect(jsonPath("$.error").value("Unauthorized"))
        .andExpect(jsonPath("$.message")
            .value("Email or password is incorrect please try again"));
  }

  @Test
  void signInRejectsTooShortPassword() throws Exception {
    mockMvc
        .perform(post("/sign-in")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"email": "%s", "password": "short"}
                """.formatted(EMAIL)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details.issues[0].path").value("password"));
  }

  @Test
  void signOutSucceedsWithoutASession() throws Exception {
    mockMvc
        .perform(post("/sign-out"))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));
  }

  @Test
  void leavesOtherEndpointsAuthenticated() throws Exception {
    mockMvc
        .perform(get("/shops"))
        .andExpect(status().isUnauthorized());
  }

  private static Profile profile() {
    Profile profile = new Profile();
    profile.setId(PROFILE_ID);
    profile.setEmail(EMAIL);
    profile.setName("Sam Barista");
    return profile;
  }

}
