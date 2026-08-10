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

import edu.cnm.deepdive.coffeeshop.model.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(
    assignableTypes = {SignUpController.class, SignInController.class, SignOutController.class})
public class AuthenticationExceptionAdvice {

  private static final String VALIDATION_FALLBACK_MESSAGE = "A validation error occurred";
  private static final String SERVER_ERROR_MESSAGE =
      "internal server error occurred try again later";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRequest(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    List<ObjectError> errors = exception.getBindingResult().getAllErrors();
    List<Map<String, String>> issues = errors
        .stream()
        .map(AuthenticationExceptionAdvice::issue)
        .toList();
    String message = errors.isEmpty()
        ? VALIDATION_FALLBACK_MESSAGE
        : errors.getFirst().getDefaultMessage();
    return response(HttpStatus.BAD_REQUEST, message, request, Map.of("issues", issues));
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatus(
      ResponseStatusException exception, HttpServletRequest request) {
    HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
    String message = (exception.getReason() != null)
        ? exception.getReason()
        : status.getReasonPhrase();
    return response(status, message, request, null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnexpected(
      Exception exception, HttpServletRequest request) {
    logger.error("Unhandled exception in an authentication endpoint", exception);
    return response(HttpStatus.INTERNAL_SERVER_ERROR, SERVER_ERROR_MESSAGE, request, null);
  }

  private static ResponseEntity<ErrorResponse> response(
      HttpStatus status, String message, HttpServletRequest request, Object details) {
    ErrorResponse body = new ErrorResponse(Instant.now(), status.value(),
        status.getReasonPhrase(), message, request.getRequestURI(), details);
    return ResponseEntity
        .status(status)
        .body(body);
  }

  private static Map<String, String> issue(ObjectError error) {
    Map<String, String> issue = new LinkedHashMap<>();
    issue.put("path", (error instanceof FieldError fieldError)
        ? fieldError.getField()
        : error.getObjectName());
    issue.put("message", error.getDefaultMessage());
    return issue;
  }

}
