package com.blogcode.studentcrudapi.exception;

import java.time.Instant;
import java.util.List;

/**
 * Uniform JSON shape returned for every error response produced by this API.
 *
 * <p>Implemented as a {@code record} because it is a simple, immutable data
 * carrier with no behaviour - constructed once by
 * {@link GlobalExceptionHandler} and immediately serialized to the HTTP
 * response body. Standardising on a single error shape across the whole API
 * (rather than letting each exception produce ad-hoc JSON) makes life much
 * easier for API consumers, who can write one piece of error-handling code
 * that works for every endpoint.
 *
 * @param status    the HTTP status code as an integer, e.g. {@code 404},
 *                  duplicated here (in addition to the actual HTTP status
 *                  line) so it is easy to inspect directly from the JSON
 *                  body without needing access to response headers.
 * @param message   a short, human-readable summary of what went wrong, e.g.
 *                  {@code "Student not found with id: 42"}.
 * @param errors    field-level validation error messages (e.g.
 *                  {@code "firstName: First name must not be blank"});
 *                  empty for non-validation errors such as a
 *                  {@link ResourceNotFoundException}.
 * @param timestamp the server time at which the error was generated, useful
 *                  for correlating client-reported issues with server logs.
 */
public record ErrorResponse(
        int status,
        String message,
        List<String> errors,
        Instant timestamp
) {
}
