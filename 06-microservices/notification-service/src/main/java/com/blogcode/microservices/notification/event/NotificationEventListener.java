package com.blogcode.microservices.notification.event;

import com.blogcode.microservices.notification.domain.NotificationChannel;
import com.blogcode.microservices.notification.dto.NotificationRequest;
import com.blogcode.microservices.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listens for {@link OrderPlacedEvent}s published elsewhere in this
 * application and reacts by creating a notification.
 *
 * <p><strong>Why this class exists:</strong> this is the core teaching
 * example of event-driven processing in this module. The HTTP layer
 * ({@code controller.NotificationEventController}) never calls
 * {@link NotificationService} directly for simulated events - it only
 * publishes an event and returns immediately. This listener processes that
 * event completely independently, on a separate thread (thanks to
 * {@link Async}), demonstrating the same decoupling a real message broker
 * (Kafka/RabbitMQ) would provide between an order service and a
 * notification service, without requiring any broker infrastructure for
 * this teaching project.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    /** Service used to actually create/store the resulting notification. */
    private final NotificationService notificationService;

    /**
     * Handles a published {@link OrderPlacedEvent} by creating an email
     * notification addressed to the customer.
     *
     * <p>Runs asynchronously (on a thread from the pool configured in
     * {@code config.AsyncConfig}) so that publishing the event never blocks
     * the caller - exactly how a message broker consumer would behave.
     *
     * @param event the order-placed occurrence to react to
     */
    @Async
    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order {}", event.orderId());
        String message = "Your order #%d for $%s has been placed successfully!"
                .formatted(event.orderId(), event.totalAmount());
        notificationService.send(new NotificationRequest(event.customerEmail(), message, NotificationChannel.EMAIL));
    }
}
