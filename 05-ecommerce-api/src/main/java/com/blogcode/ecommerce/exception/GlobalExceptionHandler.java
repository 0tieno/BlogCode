package com.blogcode.ecommerce.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Centralized exception-to-HTTP-response translator for the whole
 * application.
 *
 * <p><strong>Why this class exists:</strong> {@code @RestControllerAdvice}
 * lets every controller stay focused on the "happy path" and simply throw
 * meaningful exceptions; this single class is responsible for turning those
 * exceptions into consistent {@link ErrorResponse} JSON bodies with the
 * correct HTTP status code. Without this pattern, every controller method
 * would need its own try/catch boilerplate.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link ResourceNotFoundException}, mapping it to
     * {@code 404 Not Found}.
     *
     * @param ex      the exception thrown by a service when an entity is missing
     * @param request the originating HTTP request, used to record the path
     * @return a {@code 404} response with a descriptive {@link ErrorResponse}
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                List.of());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles Bean Validation failures raised when an invalid
     * {@code @Valid @RequestBody} is submitted, mapping them to
     * {@code 400 Bad Request} with one message per invalid field.
     *
     * @param ex      the validation exception raised by Spring MVC
     * @param request the originating HTTP request, used to record the path
     * @return a {@code 400} response listing every field validation error
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for one or more fields",
                request.getRequestURI(),
                details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles uploads that exceed the configured
     * {@code spring.servlet.multipart.max-file-size}, mapping them to
     * {@code 413 Payload Too Large} instead of letting a generic 500 leak
     * through.
     *
     * @param ex      the exception raised by the multipart resolver
     * @param request the originating HTTP request, used to record the path
     * @return a {@code 413} response describing the size limit problem
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleUploadTooLarge(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.PAYLOAD_TOO_LARGE.value(),
                HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(),
                "Uploaded file exceeds the maximum allowed size",
                request.getRequestURI(),
                List.of());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }

    /**
     * Handles {@link FileStorageException}, mapping it to
     * {@code 500 Internal Server Error} since it represents an
     * unrecoverable server-side I/O failure rather than bad client input.
     *
     * @param ex      the exception raised by the file storage service
     * @param request the originating HTTP request, used to record the path
     * @return a {@code 500} response describing the storage failure
     */
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorage(
            FileStorageException ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Catch-all handler for any exception not covered above, mapping it to
     * {@code 500 Internal Server Error}. This is the final safety net that
     * guarantees clients always receive a well-formed {@link ErrorResponse}
     * JSON body instead of a raw stack trace or a blank error page.
     *
     * @param ex      the unexpected exception
     * @param request the originating HTTP request, used to record the path
     * @return a {@code 500} response with a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI(),
                List.of());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
