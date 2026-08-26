package com.blogcode.microservices.notification.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.blogcode.microservices.notification.domain.NotificationChannel;
import com.blogcode.microservices.notification.dto.NotificationDto;
import com.blogcode.microservices.notification.dto.NotificationRequest;
import com.blogcode.microservices.notification.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link NotificationServiceImpl}, verifying that the
 * in-memory store correctly assigns identifiers and filters by recipient
 * without requiring a Spring application context.
 */
class NotificationServiceImplTest {

    private NotificationService notificationService;

    /** Creates a fresh, empty in-memory store before every test. */
    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl();
    }

    /**
     * Verifies that sending a notification assigns it an identifier and
     * immediately makes it retrievable via {@link NotificationService#getAll()}.
     */
    @Test
    void send_storesNotification_andMakesItRetrievable() {
        NotificationDto created = notificationService.send(
                new NotificationRequest("student@example.com", "Welcome!", NotificationChannel.EMAIL));

        assertThat(created.id()).isNotNull();
        assertThat(notificationService.getAll()).hasSize(1);
        assertThat(notificationService.getAll().getFirst().recipient()).isEqualTo("student@example.com");
    }

    /**
     * Verifies that {@link NotificationService#getByRecipient(String)}
     * filters case-insensitively and excludes notifications for other
     * recipients.
     */
    @Test
    void getByRecipient_filtersToMatchingRecipientOnly() {
        notificationService.send(new NotificationRequest("a@example.com", "Hi A", NotificationChannel.EMAIL));
        notificationService.send(new NotificationRequest("b@example.com", "Hi B", NotificationChannel.SMS));

        var results = notificationService.getByRecipient("A@EXAMPLE.COM");

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().message()).isEqualTo("Hi A");
    }
}
