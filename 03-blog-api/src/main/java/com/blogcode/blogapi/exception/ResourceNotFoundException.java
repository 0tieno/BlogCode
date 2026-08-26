package com.blogcode.blogapi.exception;

/**
 * Thrown whenever a client requests a resource (a post or a comment) that does not exist.
 *
 * <p>Modelling this as a dedicated, unchecked exception - rather than returning {@code null}
 * or an {@code Optional} all the way up to the controller - lets the service layer express
 * "this simply doesn't exist" as a single, easily-testable exception type, while
 * {@link GlobalExceptionHandler} takes care of translating it into a proper
 * {@code 404 Not Found} HTTP response in exactly one place.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with a human-readable message describing what was not found.
     *
     * @param message explanation of which resource was missing, suitable for API clients
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
