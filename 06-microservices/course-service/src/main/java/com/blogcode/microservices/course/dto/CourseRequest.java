package com.blogcode.microservices.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Write-model DTO accepted by the API when creating or updating a course.
 * Separated from {@link CourseDto} so validation constraints only apply to
 * client input, never to data already stored.
 *
 * @param title       required course title (max 200 characters)
 * @param description optional free-text description (max 2000 characters)
 * @param instructor  required instructor name (max 120 characters)
 * @param credits     required credit count, must be at least 1
 */
public record CourseRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @NotBlank(message = "Instructor is required")
        @Size(max = 120, message = "Instructor must be at most 120 characters")
        String instructor,

        @NotNull(message = "Credits is required")
        @Min(value = 1, message = "Credits must be at least 1")
        Integer credits) {
}
