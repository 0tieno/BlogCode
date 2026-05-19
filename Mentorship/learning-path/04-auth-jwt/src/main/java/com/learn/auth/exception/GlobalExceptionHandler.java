package com.learn.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Authentication-specific exception mapping
//
// Different business exceptions map to different HTTP status codes:
//
//   EmailAlreadyExistsException → 409 Conflict
//     "The resource you're trying to create already exists."
//
//   InvalidCredentialsException → 401 Unauthorized
//     "I don't know who you are — prove your identity first."
//
//   ValidationException         → 400 Bad Request
//     "Your request body is malformed."
//
// Without typed exceptions, the old generic RuntimeException handler mapped
// ALL of these to 400, which is wrong and confusing for API consumers.
// A client receives 401 and knows: "I need to check my token/credentials."
// A client receives 409 and knows: "This email is taken, try another."
// ══════════════════════════════════════════════════════════════════════════════
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 400 Bad Request ── validation failures
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // ── 409 Conflict ── registration with an email that already exists
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleEmailConflict(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    // ── 401 Unauthorized ── wrong email or wrong password during login
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
    }

    // ── 500 Internal Server Error ── unexpected crash (safety net)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
