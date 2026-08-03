package com.blogcode.studentcrudapi.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralised exception-to-HTTP-response translator for the whole API.
 *
 * <p>{@link RestControllerAdvice} marks this class as a global,
 * cross-cutting component that Spring MVC consults whenever <em>any</em>
 * {@code @RestController} method throws an exception. This is the
 * "exception handling" layer of the architecture: it means individual
 * controller methods (see {@code StudentController}) can stay focused on
 * the "happy path" and simply let exceptions propagate, trusting that this
 * class will catch them and produce a consistent, well-structured JSON
 * error response - instead of every controller method needing its own
 * try/catch blocks and instead of clients ever seeing a raw stack trace.
 *
 * <p>Each {@link ExceptionHandler}-annotated method below targets one
 * specific exception type and maps it to an appropriate HTTP status code
 * and {@link ErrorResponse} body.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the case where a requested student resource does not exist.
     *
     * @param exception the exception thrown by the service layer when a
     *                  lookup by id (or another key) fails to find a match.
     * @return a {@code 404 Not Found} response with a single-element
     *         {@link ErrorResponse#errors()} list describing the exception
     *         message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
        log.warn("Resource not found: {}", exception.getMessage());
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Resource not found",
                List.of(exception.getMessage()),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles Bean Validation failures raised when a {@code @Valid}
     * request body (e.g. {@code StudentRequest}) fails one or more
     * constraints declared with {@code jakarta.validation} annotations.
     *
     * <p>Spring MVC throws {@link MethodArgumentNotValidException}
     * automatically before the controller method body even executes, once
     * it detects a validation failure on an {@code @Valid}-annotated
     * parameter. This handler extracts every individual field error and
     * flattens them into readable {@code "field: message"} strings so
     * clients get precise, actionable feedback about exactly what was
     * wrong with their request.
     *
     * @param exception the exception raised by Spring's validation
     *                  machinery, containing one {@link FieldError} per
     *                  violated constraint.
     * @return a {@code 400 Bad Request} response listing every field-level
     *         validation error.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception) {
        List<String> fieldErrors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .toList();
        log.warn("Validation failed: {}", fieldErrors);
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                fieldErrors,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles constraint violations raised outside of a
     * {@code @RequestBody}-bound object - for example, on
     * {@code @RequestParam}/{@code @PathVariable} arguments validated
     * directly by the Bean Validation provider.
     *
     * @param exception the exception describing one or more constraint
     *                  violations.
     * @return a {@code 400 Bad Request} response listing every constraint
     *         violation message.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        List<String> violations = exception.getConstraintViolations().stream()
                .map(violation -> "%s: %s".formatted(violation.getPropertyPath(), violation.getMessage()))
                .toList();
        log.warn("Constraint violation: {}", violations);
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                violations,
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Catch-all handler for any exception not covered by a more specific
     * handler above.
     *
     * <p>Without this fallback, an unexpected exception (e.g. a
     * {@link NullPointerException} triggered by a bug) would either produce
     * Spring Boot's default whitelabel error page/JSON or leak internal
     * details to the client. Catching {@link Exception} broadly here and
     * returning a generic {@code 500 Internal Server Error} keeps the
     * external contract predictable while the real details are still
     * captured in server-side logs for debugging.
     *
     * @param exception any exception not already handled by a more specific
     *                  {@code @ExceptionHandler} method.
     * @return a generic {@code 500 Internal Server Error} response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception exception) {
        log.error("Unexpected error handling request", exception);
        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                List.of(exception.getMessage() == null ? "No further details available" : exception.getMessage()),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
