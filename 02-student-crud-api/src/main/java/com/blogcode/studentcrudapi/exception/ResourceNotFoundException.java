package com.blogcode.studentcrudapi.exception;

/**
 * Unchecked exception thrown when a requested {@code Student} resource
 * cannot be found by its identifier (or by another lookup key).
 *
 * <p>Extending {@link RuntimeException} rather than a checked exception is a
 * deliberate choice consistent with idiomatic Spring practice: checked
 * exceptions would force every calling method up the stack (repository ->
 * service -> controller) to either declare {@code throws} or catch them
 * immediately, cluttering method signatures for what is, in a web
 * application, simply an alternate HTTP outcome (a 404 response) rather than
 * a truly exceptional condition the caller must recover from inline.
 *
 * <p>This exception is caught centrally by
 * {@link GlobalExceptionHandler#handleResourceNotFound(ResourceNotFoundException)},
 * which converts it into a {@code 404 Not Found} JSON error response,
 * keeping the service layer free of any HTTP-specific concerns.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with a human-readable message describing
     * which resource could not be found.
     *
     * @param message a descriptive message (e.g. {@code "Student not found
     *                with id: 42"}) that is ultimately surfaced to API
     *                clients via {@link ErrorResponse#message()}.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
