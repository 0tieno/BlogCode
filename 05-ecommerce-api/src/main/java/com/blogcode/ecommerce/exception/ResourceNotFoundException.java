package com.blogcode.ecommerce.exception;

/**
 * Thrown by service implementations whenever a client requests an entity
 * (by id) that does not exist in the database.
 *
 * <p><strong>Why this class exists:</strong> a dedicated, unchecked
 * exception type lets {@link GlobalExceptionHandler} translate "not found"
 * conditions into an HTTP 404 response in exactly one place, instead of
 * every controller method having to check for {@code null} and build its
 * own {@code ResponseEntity}.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with a human-readable message describing
     * which resource was not found.
     *
     * @param message description such as {@code "Product not found with id: 42"}
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
