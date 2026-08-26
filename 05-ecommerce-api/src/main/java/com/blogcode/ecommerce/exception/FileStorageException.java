package com.blogcode.ecommerce.exception;

/**
 * Thrown when the {@code FileStorageService} cannot store or read an
 * uploaded file (e.g. disk I/O failure, invalid file name).
 *
 * <p><strong>Why this class exists:</strong> wrapping low-level
 * {@link java.io.IOException}s in a dedicated unchecked exception keeps the
 * service interface free of checked-exception clutter while still letting
 * {@link GlobalExceptionHandler} report a meaningful HTTP error to clients.
 */
public class FileStorageException extends RuntimeException {

    /**
     * Creates a new exception with a human-readable message and the
     * underlying cause (typically an {@link java.io.IOException}).
     *
     * @param message description of what went wrong while storing the file
     * @param cause   the underlying low-level exception, may be {@code null}
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
