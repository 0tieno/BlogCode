package com.blogcode.microservices.notification.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralized exception-to-HTTP-response translator for notification-service.
 *
 * <p><strong>Why this class exists:</strong> a {@code @RestControllerAdvice}
 * intercepts exceptions thrown by any controller in this module and
 * converts them into the consistent {@link ErrorResponse} shape, so
 * controllers themselves never need try/catch blocks - the same pattern
 * applied consistently across every module in this curriculum.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation failures (triggered by {@code @Valid} on
     * request bodies), collecting every field error into one readable
     * message instead of exposing Spring's raw exception structure.
     *
     * @param ex      the validation failure raised by Spring MVC
     * @param request the failing HTTP request, used to populate the error path
     * @return HTTP 400 with a summary of every field validation failure
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ErrorResponse body = new ErrorResponse(
                Instant.now(), HttpStatus.BAD_REQUEST.value(), "Validation Failed", message, request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles every other unexpected exception as a fallback, ensuring the
     * client always receives a structured JSON error instead of a raw
     * stack trace.
     *
     * @param ex      the unexpected exception
     * @param request the failing HTTP request, used to populate the error path
     * @return HTTP 500 with a generic error description
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.internalServerError().body(body);
    }
}
