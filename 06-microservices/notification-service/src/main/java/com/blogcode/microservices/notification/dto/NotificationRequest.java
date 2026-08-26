package com.blogcode.microservices.notification.dto;

import com.blogcode.microservices.notification.domain.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Inbound payload used to directly request that a notification be created
 * and (simulated to be) delivered.
 *
 * <p><strong>Why this class exists:</strong> separating the request shape
 * from {@link NotificationDto} means the client cannot supply a
 * server-assigned field such as {@code id} or {@code createdAt}, and lets
 * validation annotations live only on the input side.
 *
 * @param recipient address to deliver the notification to; required
 * @param message   human-readable body of the notification; required
 * @param channel   delivery channel to simulate sending through; required
 */
public record NotificationRequest(
        @NotBlank(message = "recipient is required") String recipient,
        @NotBlank(message = "message is required") String message,
        @NotNull(message = "channel is required") NotificationChannel channel) {
}
