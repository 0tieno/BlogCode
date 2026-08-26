package com.blogcode.microservices.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the course-service module.
 *
 * <p><strong>Why this class exists:</strong> this service owns the
 * {@code Course} data domain exclusively - no other service is allowed to
 * touch the {@code course_db} database directly. Any other service that
 * needs course data (see {@code student-service}'s {@code CourseClient})
 * must go through this service's REST API, which is the essence of the
 * "database per service" microservices pattern.
 *
 * <p>Simply having {@code spring-cloud-starter-netflix-eureka-client} on
 * the classpath is enough for this application to auto-register itself
 * with the {@code service-registry} on startup; no additional annotation
 * is required in modern Spring Cloud versions.
 */
@SpringBootApplication
public class CourseServiceApplication {

    /**
     * Boots the course-service application context and embedded servlet
     * container.
     *
     * @param args standard command-line arguments, forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }
}
