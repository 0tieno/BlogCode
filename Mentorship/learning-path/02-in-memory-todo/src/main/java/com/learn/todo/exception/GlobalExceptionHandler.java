package com.learn.todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Typed Exception Handlers — the right HTTP status for each error
//
// The key improvement over a single catch-all RuntimeException handler:
//
//   Before (naive):   all errors → 400 Bad Request
//   After  (correct): each error type maps to its own HTTP status:
//
//     TodoNotFoundException   → 404 Not Found    (the resource is missing)
//     ValidationException     → 400 Bad Request  (the request body is wrong)
//
// Spring evaluates handlers from MOST specific to LEAST specific, so:
//   TodoNotFoundException (more specific) is caught first.
//   RuntimeException      (less specific) is only caught if nothing else matches.
// ══════════════════════════════════════════════════════════════════════════════
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 400 Bad Request ── validation failures (@Valid + @NotBlank, @Size, etc.)
    // Returns a clean map of { "fieldName": "error message" }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        // Example response: { "title": "Title is required" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // ── 404 Not Found ── a specific todo was requested but doesn't exist
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(TodoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    // ── 500 Internal Server Error ── unexpected crash (safety net)
    // Any RuntimeException NOT caught above falls here.
    // In production you would log this with a log.error() call (see Project 03+).
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
