package com.blogcode.microservices.notification.service;

import com.blogcode.microservices.notification.dto.NotificationDto;
import com.blogcode.microservices.notification.dto.NotificationRequest;
import java.util.List;

/**
 * Service-layer contract for creating and querying notifications.
 *
 * <p><strong>Why this class exists:</strong> defining the contract as an
 * interface (implemented by {@code impl.NotificationServiceImpl}) keeps the
 * controller layer decoupled from the storage mechanism, consistent with
 * every other service in this curriculum - even though this particular
 * implementation happens to use an in-memory map instead of a database.
 */
public interface NotificationService {

    /**
     * Creates and (simulated to be) delivers a new notification.
     *
     * @param request recipient, message, and channel to send through
     * @return the created notification, including its assigned identifier
     */
    NotificationDto send(NotificationRequest request);

    /**
     * Retrieves every notification created since the application started.
     *
     * @return all stored notifications, most recent first
     */
    List<NotificationDto> getAll();

    /**
     * Retrieves every notification sent to a specific recipient.
     *
     * @param recipient the recipient address to filter by
     * @return notifications sent to that recipient, most recent first
     */
    List<NotificationDto> getByRecipient(String recipient);
}
