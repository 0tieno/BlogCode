package com.blogcode.microservices.student.client;

import com.blogcode.microservices.student.dto.CourseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Declarative HTTP client for calling course-service.
 *
 * <p><strong>Why this class exists:</strong> OpenFeign lets us describe an
 * HTTP API as a plain Java interface annotated with familiar Spring MVC
 * mapping annotations; Spring generates a working proxy implementation at
 * startup, so calling {@code courseClient.getCourseById(5L)} looks and
 * feels exactly like calling any other local Java method, even though it
 * actually performs an HTTP GET to whatever host Eureka currently reports
 * for {@code course-service}.
 *
 * <p>{@code fallback = CourseClientFallback.class} wires in Spring Cloud
 * CircuitBreaker's Feign integration (enabled via
 * {@code feign.circuitbreaker.enabled=true} in {@code application.yml}):
 * if calling course-service fails or times out repeatedly, the circuit
 * breaker "opens" and every call is instantly redirected to
 * {@link CourseClientFallback} instead of continuing to hammer (and wait
 * on) a struggling downstream service - a small but realistic taste of
 * resilience engineering.
 */
@FeignClient(name = "course-service", fallback = CourseClientFallback.class)
public interface CourseClient {

    /**
     * Fetches a single course by id from course-service.
     *
     * @param id the course id to fetch
     * @return the matching course, or a fallback placeholder if
     *         course-service is unavailable
     */
    @GetMapping("/api/courses/{id}")
    CourseDto getCourseById(@PathVariable("id") Long id);
}
