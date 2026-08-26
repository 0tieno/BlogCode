package com.blogcode.microservices.notification.domain;

/**
 * Enumerates the delivery channel a {@code Notification} was (simulated to
 * be) sent through.
 *
 * <p><strong>Why this class exists:</strong> even a tiny in-memory teaching
 * service benefits from a fixed, typed vocabulary instead of a raw string,
 * the same lesson taught by module 5's {@code OrderStatus} enum.
 */
public enum NotificationChannel {

    /** Simulated email delivery. */
    EMAIL,

    /** Simulated SMS delivery. */
    SMS,

    /** Simulated push notification delivery. */
    PUSH
}
