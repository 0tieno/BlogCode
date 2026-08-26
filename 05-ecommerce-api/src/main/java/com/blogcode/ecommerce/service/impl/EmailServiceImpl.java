package com.blogcode.ecommerce.service.impl;

import com.blogcode.ecommerce.domain.Order;
import com.blogcode.ecommerce.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Simulated {@link EmailService} implementation used for teaching purposes.
 *
 * <p><strong>Why this class exists:</strong> configuring a real SMTP server
 * would distract from the actual lesson of this module - asynchronous
 * processing with {@code @Async}. This implementation logs the "sent"
 * email and sleeps briefly to stand in for realistic network latency,
 * making it obvious (via the log timestamps) that the HTTP response for
 * {@code POST /api/v1/orders} returns before this method finishes.
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    /**
     * {@inheritDoc}
     *
     * <p>{@code @Async} tells Spring to run this method on a separate
     * thread from a task executor (see {@code AsyncConfig}), completely
     * decoupled from the calling HTTP request thread. Any exception thrown
     * here would otherwise be silently swallowed by the default async
     * exception handler, which is why we log defensively instead of letting
     * a failure here ever affect order creation.
     */
    @Async
    @Override
    public void sendOrderConfirmation(Order order) {
        try {
            // Simulates the latency of a real network call to an email
            // provider (e.g. SendGrid, SES) without requiring one.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Email simulation was interrupted for order {}", order.getId());
            return;
        }
        log.info(
                "[SIMULATED EMAIL] To: {} | Subject: Order #{} confirmed | Total: {} | Thread: {}",
                order.getCustomerEmail(),
                order.getId(),
                order.getTotalAmount(),
                Thread.currentThread().getName());
    }
}
