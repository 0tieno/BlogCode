package com.blogcode.microservices.course.dto;

import java.time.Instant;

/**
 * Read-model DTO returned to API clients (including {@code student-service}'s
 * Feign client) whenever a {@code Course} is exposed over HTTP.
 *
 * <p>As in module 5, we never serialize the JPA entity directly - this
 * record is the stable, public contract of the course-service API.
 *
 * @param id          database identifier of the course
 * @param title       course title
 * @param description longer free-text description
 * @param instructor  name of the instructor teaching the course
 * @param credits     number of academic credits
 * @param createdAt   when the course was first created
 * @param updatedAt   when the course was last modified
 */
public record CourseDto(
        Long id,
        String title,
        String description,
        String instructor,
        Integer credits,
        Instant createdAt,
        Instant updatedAt) {
}
