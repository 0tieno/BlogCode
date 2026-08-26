package com.blogcode.microservices.notification.service.impl;

import com.blogcode.microservices.notification.domain.Notification;
import com.blogcode.microservices.notification.dto.NotificationDto;
import com.blogcode.microservices.notification.dto.NotificationRequest;
import com.blogcode.microservices.notification.service.NotificationService;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * In-memory implementation of {@link NotificationService}.
 *
 * <p><strong>Why this class exists:</strong> it stores notifications in a
 * {@link ConcurrentHashMap} rather than a database, deliberately teaching
 * that services can trade durability for simplicity when the data is
 * transient or when persistence is genuinely not a requirement. All state
 * here is lost on restart - acceptable for a teaching notification service,
 * but a design decision that would need revisiting in production (e.g. by
 * adding a database or delegating to a managed notification/email
 * provider).
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    /** Thread-safe in-memory store, keyed by generated notification id. */
    private final Map<Long, Notification> store = new ConcurrentHashMap<>();

    /** Generates unique, monotonically increasing identifiers for new notifications. */
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * {@inheritDoc}
     *
     * <p>Logs the "delivery" at INFO level to make the simulated send
     * visible in the console, mirroring how {@code EmailServiceImpl} in
     * module 5 simulates sending email without a real SMTP server.
     */
    @Override
    public NotificationDto send(NotificationRequest request) {
        Notification notification = Notification.builder()
                .id(idGenerator.getAndIncrement())
                .recipient(request.recipient())
                .message(request.message())
                .channel(request.channel())
                .createdAt(Instant.now())
                .build();
        store.put(notification.getId(), notification);
        log.info(
                "Simulated {} notification sent to {}: \"{}\"",
                notification.getChannel(),
                notification.getRecipient(),
                notification.getMessage());
        return toDto(notification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NotificationDto> getAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(this::toDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<NotificationDto> getByRecipient(String recipient) {
        return store.values().stream()
                .filter(n -> n.getRecipient().equalsIgnoreCase(recipient))
                .sorted(Comparator.comparing(Notification::getCreatedAt).reversed())
                .map(this::toDto)
                .toList();
    }

    /**
     * Converts a {@link Notification} to its outward-facing {@link NotificationDto}.
     *
     * @param notification the in-memory notification to convert
     * @return the corresponding DTO
     */
    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getRecipient(),
                notification.getMessage(),
                notification.getChannel(),
                notification.getCreatedAt());
    }
}
