package com.blogcode.microservices.student.client;

import com.blogcode.microservices.student.dto.CourseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Resilience fallback for {@link CourseClient}, returned whenever
 * course-service is unreachable, slow, or the circuit breaker protecting
 * calls to it is open.
 *
 * <p><strong>Why this class exists:</strong> without a fallback, a single
 * struggling downstream service (course-service) would cause every request
 * to student-service that needs course details to also fail - a classic
 * cascading failure. Returning a safe, clearly-labeled placeholder instead
 * lets {@code StudentServiceImpl} keep working in a degraded mode: student
 * data is still returned, just without live course details.
 */
@Slf4j
@Component
public class CourseClientFallback implements CourseClient {

    /**
     * {@inheritDoc}
     *
     * <p>Instead of propagating the failure, logs it and returns a
     * placeholder {@link CourseDto} that is obviously not real data (its
     * title says so), so API consumers can distinguish "no course" from
     * "course-service was down when we asked".
     */
    @Override
    public CourseDto getCourseById(Long id) {
        log.warn("course-service is unavailable; returning fallback course data for id {}", id);
        return new CourseDto(id, "Course information temporarily unavailable", "Unknown", 0, null);
    }
}
