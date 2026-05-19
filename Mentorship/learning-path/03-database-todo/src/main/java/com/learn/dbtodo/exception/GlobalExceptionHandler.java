package com.learn.dbtodo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: GlobalExceptionHandler with typed handlers
//
// By adding @ExceptionHandler(TodoNotFoundException.class), Spring will:
//   1. Intercept any TodoNotFoundException thrown anywhere in the app.
//   2. Call THIS method instead of Spring's default error handler.
//   3. Return a clean 404 JSON response.
//
// HTTP status reference for "business logic" errors in REST APIs:
//
//   400 Bad Request   — invalid input from the client (validation failures)
//   404 Not Found     — the requested resource doesn't exist
//   409 Conflict      — the request conflicts with existing state (duplicate email)
//   401 Unauthorized  — not authenticated (wrong login credentials)
//   500 Server Error  — something crashed unexpectedly on our side
// ══════════════════════════════════════════════════════════════════════════════
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 400 Bad Request ── @Valid failed on a request body field
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // ── 404 Not Found ── a todo with the given id doesn't exist
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(TodoNotFoundException ex) {
        // ex.getMessage() = "Todo not found: 42" — specific and useful
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    // ── Safety net ── any other RuntimeException we didn't anticipate
    // Returns 500 instead of leaking internal stack traces to the client.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(RuntimeException ex) {
        // In production: log.error("Unexpected error", ex); ← log the full trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
