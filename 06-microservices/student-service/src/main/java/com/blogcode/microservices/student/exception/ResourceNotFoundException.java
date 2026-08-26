package com.blogcode.microservices.student.exception;

/**
 * Thrown by {@code StudentServiceImpl} whenever a client requests a student
 * (by id) that does not exist in the database. See course-service's
 * {@code ResourceNotFoundException} for the detailed rationale.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with a human-readable message describing
     * which resource was not found.
     *
     * @param message description such as {@code "Student not found with id: 3"}
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
