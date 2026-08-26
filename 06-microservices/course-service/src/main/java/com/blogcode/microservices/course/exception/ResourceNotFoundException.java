package com.blogcode.microservices.course.exception;

/**
 * Thrown by {@code CourseServiceImpl} whenever a client requests a course
 * (by id) that does not exist in the database.
 *
 * <p>See module 5's {@code ResourceNotFoundException} for the detailed
 * rationale behind this pattern; it is repeated here per-service rather
 * than shared, since each microservice is meant to be independently
 * deployable and should not share a common library for such a small type.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with a human-readable message describing
     * which resource was not found.
     *
     * @param message description such as {@code "Course not found with id: 7"}
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
