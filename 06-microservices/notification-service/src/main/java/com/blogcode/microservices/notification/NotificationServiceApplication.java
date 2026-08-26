package com.blogcode.microservices.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the notification-service module.
 *
 * <p><strong>Why this class exists:</strong> unlike student-service and
 * course-service, this module has no database at all - it exists purely to
 * demonstrate reacting to events and delivering (simulated) notifications,
 * kept intentionally simple so students can focus on the event-driven
 * pattern in {@code event.NotificationEventListener} rather than
 * persistence concerns.
 */
@SpringBootApplication
public class NotificationServiceApplication {

    /**
     * Boots the notification-service application context and embedded
     * servlet container.
     *
     * @param args standard command-line arguments, forwarded to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
