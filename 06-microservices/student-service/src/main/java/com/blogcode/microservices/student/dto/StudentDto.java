package com.blogcode.microservices.student.dto;

import java.time.Instant;

/**
 * Read-model DTO returned to API clients whenever a {@code Student} is
 * exposed over HTTP, enriched with the student's enrolled course details
 * (fetched live from course-service via {@code CourseClient}).
 *
 * @param id               database identifier of the student
 * @param firstName        student's given name
 * @param lastName         student's family name
 * @param email            student's contact email address
 * @param enrolledCourseId raw course id stored locally, or {@code null}
 * @param enrolledCourse   the resolved course details, or {@code null} if
 *                         the student is not enrolled or course-service was
 *                         unreachable (see {@code CourseClientFallback})
 * @param createdAt        when the student record was first created
 * @param updatedAt        when the student record was last modified
 */
public record StudentDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        Long enrolledCourseId,
        CourseDto enrolledCourse,
        Instant createdAt,
        Instant updatedAt) {
}
