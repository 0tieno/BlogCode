package com.blogcode.microservices.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Inbound payload used to simulate an "order placed" domain event arriving
 * from another service (in this teaching project, submitted directly via
 * curl instead of through a real message broker).
 *
 * <p><strong>Why this class exists:</strong> in a production system, this
 * data would arrive as a Kafka/RabbitMQ message published by
 * {@code 05-ecommerce-api} or a dedicated order-service. Modelling it as a
 * REST payload here keeps the curriculum's infrastructure footprint small
 * (no broker to install) while still teaching the event-driven shape:
 * an external trigger causes an internal {@code ApplicationEvent} to be
 * published and handled asynchronously - see
 * {@code event.NotificationEventListener}.
 *
 * @param orderId       identifier of the order that was placed
 * @param customerEmail email address of the customer to notify
 * @param totalAmount   total order amount, included in the notification message
 */
public record OrderPlacedEventRequest(
        @NotNull(message = "orderId is required") @Positive Long orderId,
        @NotBlank(message = "customerEmail is required") String customerEmail,
        @NotNull(message = "totalAmount is required") @Positive BigDecimal totalAmount) {
}
