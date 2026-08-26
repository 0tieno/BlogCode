package com.blogcode.microservices.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Write-model DTO accepted by the API when creating or updating a student.
 *
 * @param firstName        required given name
 * @param lastName         required family name
 * @param email            required, valid email address
 * @param enrolledCourseId optional id of a course (owned by course-service)
 *                         the student is enrolling in; may be {@code null}
 */
public record StudentRequest(
        @NotBlank(message = "First name is required") String firstName,
        @NotBlank(message = "Last name is required") String lastName,
        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,
        Long enrolledCourseId) {
}
