package com.blogcode.microservices.notification.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Plain in-memory model representing a single notification that was (or is
 * about to be) delivered to a recipient.
 *
 * <p><strong>Why this class exists:</strong> unlike every other domain
 * class in this curriculum, {@code Notification} is deliberately
 * <em>not</em> a JPA {@code @Entity} - this service has no database.
 * Keeping it a plain Lombok-powered POJO, held in an in-memory store (see
 * {@code service.impl.NotificationServiceImpl}), demonstrates that not
 * every microservice needs a full persistence layer: some services are
 * legitimately simple, stateless (or intentionally volatile-state)
 * processors.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    /** In-memory identifier, assigned by the store when the notification is saved. */
    private Long id;

    /** Email address (or other channel-specific address) of the recipient. */
    private String recipient;

    /** Human-readable notification body. */
    private String message;

    /** Delivery channel this notification was (simulated to be) sent through. */
    private NotificationChannel channel;

    /** When this notification was created/delivered. */
    private Instant createdAt;
}
