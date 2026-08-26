package com.blogcode.microservices.student.dto;

import java.time.Instant;

/**
 * Local copy of course-service's read-model, representing exactly the
 * subset of course data this service needs.
 *
 * <p><strong>Why this class exists:</strong> in a microservices system,
 * services intentionally do <em>not</em> share a common DTO/entity library.
 * Sharing code creates hidden coupling: if course-service changed a shared
 * {@code CourseDto} class, every consumer would need to redeploy in
 * lock-step. Instead, {@code student-service} defines its own, minimal
 * contract describing what it expects course-service's API to look like;
 * {@link com.blogcode.microservices.student.client.CourseClient} simply
 * deserializes course-service's JSON response into this local record.
 *
 * @param id          identifier of the course, as returned by course-service
 * @param title       course title
 * @param instructor  name of the instructor teaching the course
 * @param credits     number of academic credits
 * @param createdAt   when the course was first created (informational only)
 */
public record CourseDto(Long id, String title, String instructor, Integer credits, Instant createdAt) {
}
