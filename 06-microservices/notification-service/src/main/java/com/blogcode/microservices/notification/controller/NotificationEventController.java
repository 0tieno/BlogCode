package com.blogcode.microservices.notification.controller;

import com.blogcode.microservices.notification.dto.OrderPlacedEventRequest;
import com.blogcode.microservices.notification.event.OrderPlacedEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that simulates an inbound domain event arriving at this
 * service and hands it off to be processed asynchronously.
 *
 * <p><strong>Why this class exists:</strong> in a production system, an
 * event like "order placed" would arrive via a message broker topic, not
 * an HTTP call. This controller stands in for that broker consumer: it
 * accepts the event payload over curl-friendly REST, immediately publishes
 * an internal {@link OrderPlacedEvent} via {@link ApplicationEventPublisher},
 * and returns HTTP 202 Accepted - signalling that the request was received
 * but processing (handled by {@code event.NotificationEventListener}) will
 * complete asynchronously, exactly like a fire-and-forget broker publish.
 */
@RestController
@RequestMapping("/api/v1/notifications/events")
@RequiredArgsConstructor
public class NotificationEventController {

    /** Spring's built-in event bus, used here to decouple publishing from processing. */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Simulates an "order placed" event arriving from another service (in a
     * real system, this would be module 5's ecommerce API publishing to a
     * message broker instead of calling this endpoint).
     *
     * @param request the simulated order-placed payload
     * @return HTTP 202 Accepted, since processing happens asynchronously
     */
    @PostMapping("/order-placed")
    public ResponseEntity<Void> simulateOrderPlaced(@Valid @RequestBody OrderPlacedEventRequest request) {
        eventPublisher.publishEvent(
                new OrderPlacedEvent(request.orderId(), request.customerEmail(), request.totalAmount()));
        return ResponseEntity.accepted().build();
    }
}
