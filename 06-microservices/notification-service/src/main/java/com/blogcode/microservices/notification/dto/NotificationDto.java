package com.blogcode.microservices.notification.dto;

import com.blogcode.microservices.notification.domain.NotificationChannel;
import java.time.Instant;

/**
 * Read-only representation of a {@link com.blogcode.microservices.notification.domain.Notification}
 * returned by the API.
 *
 * <p><strong>Why this class exists:</strong> a record keeps the response
 * shape immutable and self-documenting, consistent with the DTO pattern
 * used throughout the curriculum to decouple the wire format from the
 * internal domain model.
 *
 * @param id        in-memory identifier of the notification
 * @param recipient address the notification was sent to
 * @param message   human-readable body of the notification
 * @param channel   delivery channel used
 * @param createdAt when the notification was created
 */
public record NotificationDto(
        Long id,
        String recipient,
        String message,
        NotificationChannel channel,
        Instant createdAt) {
}
