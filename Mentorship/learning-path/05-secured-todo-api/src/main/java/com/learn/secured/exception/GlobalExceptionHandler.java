package com.learn.secured.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Full exception-to-status mapping table for a secured REST API
//
//   Exception                   → HTTP Status       → Meaning
//   ─────────────────────────────────────────────────────────────
//   MethodArgumentNotValidException → 400 Bad Request  → invalid request body
//   TodoNotFoundException           → 404 Not Found     → todo doesn't exist
//   EmailAlreadyExistsException     → 409 Conflict      → email already taken
//   InvalidCredentialsException     → 401 Unauthorized  → login failed
//   RuntimeException (catch-all)    → 500 Server Error  → unexpected crash
//
// This is the "contract" your API makes with its consumers. A well-designed
// API returns consistent, predictable status codes — never just 400 for
// everything regardless of what actually went wrong.
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

    // ── 404 Not Found ── a todo doesn't exist or doesn't belong to the caller
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(TodoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    // ── 409 Conflict ── registration with an already-registered email
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleConflict(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    // ── 401 Unauthorized ── login credentials are wrong
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
    }

    // ── 500 Internal Server Error ── catch-all safety net
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
