package com.blogcode.microservices.course.exception;

import java.time.Instant;
import java.util.List;

/**
 * Uniform error payload returned by every handler in
 * {@link GlobalExceptionHandler}. See module 5's {@code ErrorResponse} for
 * the detailed rationale behind giving every error response the same shape.
 *
 * @param timestamp when the error occurred
 * @param status    HTTP status code, e.g. 404
 * @param error     short HTTP reason phrase, e.g. "Not Found"
 * @param message   human-readable summary of what went wrong
 * @param path      the request URI that triggered the error
 * @param details   optional list of field-level validation messages
 */
public record ErrorResponse(
        Instant timestamp, int status, String error, String message, String path, List<String> details) {
}
