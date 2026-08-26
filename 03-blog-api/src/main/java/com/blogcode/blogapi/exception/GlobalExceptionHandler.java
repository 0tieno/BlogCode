package com.blogcode.blogapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralised exception-to-HTTP-response translator for the entire Blog API.
 *
 * <p>{@code @RestControllerAdvice} lets a single class intercept exceptions thrown from
 * <i>any</i> {@code @RestController} in the application, instead of every controller
 * method needing its own {@code try/catch} blocks. This keeps controllers focused purely
 * on request/response mapping, while all error-formatting logic lives in one obvious
 * place - a cornerstone pattern in layered Spring Boot applications.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link ResourceNotFoundException}, returned whenever a requested post or
     * comment does not exist, translating it into a {@code 404 Not Found} response.
     *
     * @param ex      the exception raised by the service layer
     * @param request the failing HTTP request, used to populate the error's {@code path}
     * @return a {@code 404} response with a uniform {@link ErrorResponse} body
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
                                                                 HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles Bean Validation failures raised when an {@code @Valid} request DTO fails
     * one or more constraints (e.g. a blank title), translating them into a
     * {@code 400 Bad Request} response that lists every failing field individually so
     * clients can display precise, actionable error messages.
     *
     * @param ex      the exception raised by Spring's validation machinery
     * @param request the failing HTTP request, used to populate the error's {@code path}
     * @return a {@code 400} response whose {@code fieldErrors} map names each invalid
     *         field alongside its validation message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                           HttpServletRequest request) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null
                                ? "Invalid value"
                                : fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for one or more fields",
                request.getRequestURI(),
                fieldErrors
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Catch-all fallback for any exception not handled more specifically above, translated
     * into a generic {@code 500 Internal Server Error}. This guarantees that even unexpected
     * bugs still produce a well-formed {@link ErrorResponse} instead of leaking a raw stack
     * trace to API clients.
     *
     * @param ex      the unexpected exception
     * @param request the failing HTTP request, used to populate the error's {@code path}
     * @return a {@code 500} response with a uniform {@link ErrorResponse} body
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex,
                                                                  HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
