package com.blogcode.microservices.notification.event;

import java.math.BigDecimal;

/**
 * Internal application event published when an "order placed" occurrence
 * needs to trigger a notification.
 *
 * <p><strong>Why this class exists:</strong> using a plain record as an
 * event payload (rather than extending Spring's {@code ApplicationEvent})
 * is the modern idiom supported since Spring Framework 4.2 - any POJO can
 * be published via {@code ApplicationEventPublisher.publishEvent(Object)}
 * and routed to matching {@code @EventListener} methods. This decouples
 * the HTTP layer ({@code controller.NotificationEventController}, which
 * publishes the event) from the processing layer
 * ({@code event.NotificationEventListener}, which reacts to it
 * asynchronously) - the same decoupling a real message broker would
 * provide, just in-process.
 *
 * @param orderId       identifier of the order that was placed
 * @param customerEmail email address of the customer to notify
 * @param totalAmount   total order amount
 */
public record OrderPlacedEvent(Long orderId, String customerEmail, BigDecimal totalAmount) {
}
