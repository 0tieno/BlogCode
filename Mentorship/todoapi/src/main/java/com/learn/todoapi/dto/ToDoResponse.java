package com.learn.todoapi.dto;

import java.time.LocalDateTime;

public record ToDoResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt
) {
}
