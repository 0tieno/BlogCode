package com.blogcode.microservices.notification.controller;

import com.blogcode.microservices.notification.dto.NotificationDto;
import com.blogcode.microservices.notification.dto.NotificationRequest;
import com.blogcode.microservices.notification.service.NotificationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing direct notification creation and query
 * endpoints.
 *
 * <p><strong>Why this class exists:</strong> beyond reacting to simulated
 * domain events (see {@link NotificationEventController}), a notification
 * service also typically needs a synchronous API other services (or
 * administrators) can call directly to send an arbitrary notification -
 * this controller covers that use case. As with every controller in this
 * curriculum, it delegates all business logic to the {@link
 * NotificationService} interface and stays limited to
 * HTTP concerns (status codes, request/response mapping).
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    /** Service layer handling notification creation and storage. */
    private final NotificationService notificationService;

    /**
     * Creates and (simulated to be) delivers a notification directly.
     *
     * @param request validated recipient/message/channel payload
     * @return HTTP 201 with the created notification
     */
    @PostMapping
    public ResponseEntity<NotificationDto> send(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.send(request));
    }

    /**
     * Lists every notification created since the application started.
     *
     * @return all stored notifications, most recent first
     */
    @GetMapping
    public List<NotificationDto> getAll() {
        return notificationService.getAll();
    }

    /**
     * Lists every notification sent to a specific recipient.
     *
     * @param recipient the recipient address to filter by
     * @return notifications sent to that recipient, most recent first
     */
    @GetMapping("/recipients/{recipient}")
    public List<NotificationDto> getByRecipient(@PathVariable String recipient) {
        return notificationService.getByRecipient(recipient);
    }
}
