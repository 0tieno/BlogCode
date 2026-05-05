package com.kampuni.todo_list_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// ✅ BEST PRACTICE: @RestControllerAdvice catches exceptions thrown anywhere in your controllers
//    and converts them into clean JSON error responses instead of ugly HTML stack traces.
//
//    Without this, if a user sends bad input, the client receives a 500 error with no useful message.
//    With this, they receive a clear 400 error explaining exactly what went wrong.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ Handles @Valid validation failures (e.g., blank name, invalid email format).
    //    Returns a map of field -> error message, e.g.:
    //    { "email": "Enter a valid email", "password": "Password must be at least 8 characters" }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // ✅ Handles business exceptions like "Email already exists" or "Invalid credentials".
    //    Returns: { "error": "Email already exists" }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    // ✅ Catch-all for any unexpected exception — returns 500 instead of leaking stack traces.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}

