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
// CONCEPT: Global Exception Handler
//
// Without this, when @Valid fails, Spring returns a 400 with a MASSIVE ugly JSON
// blob that's hard to read. With this, we control exactly what the client sees.
//
// @RestControllerAdvice = "intercept exceptions from any controller"
// @ExceptionHandler      = "handle this specific type of exception"
// ══════════════════════════════════════════════════════════════════════════════
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation failures (@Valid + @NotBlank, @Size, etc.)
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
}

