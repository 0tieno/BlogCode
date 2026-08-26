package com.blogcode.microservices.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the student-service module.
 *
 * <p><strong>Why this class exists:</strong> {@code @EnableFeignClients}
 * activates Spring Cloud OpenFeign's component scanning for interfaces
 * annotated {@code @FeignClient} (see {@code client.CourseClient}),
 * generating a working HTTP client implementation for each one at startup.
 * Without this annotation, {@code CourseClient} would just be an unused
 * interface - Spring would never know to create a bean for it.
 */
@SpringBootApplication
@EnableFeignClients
public class StudentServiceApplication {

    /**
     * Boots the student-service application context and embedded servlet
     * container.
     *
     * @param args standard command-line arguments, forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
