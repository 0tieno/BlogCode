package com.blogcode.ecommerce.service;

import com.blogcode.ecommerce.domain.Order;

/**
 * Business-logic contract for sending customer-facing emails.
 *
 * <p><strong>Why this class exists:</strong> the curriculum needs to teach
 * asynchronous processing ({@code @Async}) without requiring students to
 * configure a real SMTP server. Modeling email sending behind an interface
 * means the "simulated" implementation used here
 * ({@code EmailServiceImpl}, which just logs and sleeps briefly to mimic
 * network latency) could be swapped for a real
 * {@code JavaMailSender}-backed implementation later without touching
 * {@code OrderServiceImpl}.
 */
public interface EmailService {

    /**
     * Sends (or, in this teaching implementation, simulates sending) an
     * order confirmation email to the customer who placed the order.
     *
     * <p>Implementations are expected to be annotated {@code @Async} so
     * that placing an order returns to the HTTP client immediately instead
     * of blocking on email delivery latency.
     *
     * @param order the order that was just placed
     */
    void sendOrderConfirmation(Order order);
}
