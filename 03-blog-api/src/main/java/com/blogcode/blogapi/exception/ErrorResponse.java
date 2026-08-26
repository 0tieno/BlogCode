package com.blogcode.blogapi.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Standard, uniform error body returned by every failed API call in this project.
 *
 * <p>Without a shared error shape, every endpoint would be free to invent its own error
 * JSON, which makes client-side error handling inconsistent and painful. Returning the
 * same {@code ErrorResponse} structure from {@link GlobalExceptionHandler} - no matter
 * which exception triggered it - gives API consumers one predictable contract to code
 * against.
 *
 * @param timestamp    when the error occurred
 * @param status       the HTTP status code, e.g. 404
 * @param error        the HTTP status reason phrase, e.g. "Not Found"
 * @param message      a human-readable description of what went wrong
 * @param path         the request URI that triggered the error
 * @param fieldErrors  optional map of field name -> validation message, populated only for
 *                     request body validation failures; {@code null} otherwise
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {
}
