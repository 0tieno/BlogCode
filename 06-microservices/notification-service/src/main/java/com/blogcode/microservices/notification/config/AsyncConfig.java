package com.blogcode.microservices.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Enables Spring's asynchronous method execution support so that
 * {@code event.NotificationEventListener} can process events on a
 * background thread.
 *
 * <p><strong>Why this class exists:</strong> {@code @EnableAsync} must be
 * present somewhere in the application context for {@code @Async} methods
 * to actually run asynchronously - without it, {@code @Async} is silently
 * ignored and methods run synchronously on the calling thread. This is the
 * same pattern used by {@code AsyncConfig} in module 5's
 * {@code 05-ecommerce-api} project.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
