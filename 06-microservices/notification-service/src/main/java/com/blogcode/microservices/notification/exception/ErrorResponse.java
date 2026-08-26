package com.blogcode.microservices.notification.exception;

import java.time.Instant;

/**
 * Standard error payload returned to clients whenever a request cannot be
 * processed, whether due to invalid input or an internal failure.
 *
 * <p><strong>Why this class exists:</strong> a single, predictable error
 * shape across every endpoint makes the API far easier to consume than ad
 * hoc exception messages - the same rationale documented in module 5's
 * {@code exception.ErrorResponse}.
 *
 * @param timestamp when the error occurred
 * @param status    HTTP status code
 * @param error     short machine-friendly error category
 * @param message   human-readable explanation of what went wrong
 * @param path      request path that produced the error
 */
public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {
}
