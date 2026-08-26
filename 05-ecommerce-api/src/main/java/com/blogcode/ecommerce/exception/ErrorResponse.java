package com.blogcode.ecommerce.exception;

import java.time.Instant;
import java.util.List;

/**
 * Uniform error payload returned by every handler in
 * {@link GlobalExceptionHandler}.
 *
 * <p><strong>Why this class exists:</strong> API clients (and the students
 * consuming this curriculum) benefit from every error response - whether a
 * 404, a validation failure, or an unexpected 500 - having the exact same
 * JSON shape. That predictability is what makes an API pleasant to
 * integrate against.
 *
 * @param timestamp when the error occurred
 * @param status    HTTP status code, e.g. 404
 * @param error     short HTTP reason phrase, e.g. "Not Found"
 * @param message   human-readable summary of what went wrong
 * @param path      the request URI that triggered the error
 * @param details   optional list of field-level validation messages, empty
 *                  for non-validation errors
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details) {
}
