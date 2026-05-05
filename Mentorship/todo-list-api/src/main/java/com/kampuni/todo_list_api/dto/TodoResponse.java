package com.kampuni.todo_list_api.dto;

import java.time.LocalDateTime;

// ✅ BEST PRACTICE: The response DTO should mirror what the client needs to display.
//    Never return the entity directly — it can expose sensitive data or cause serialization issues.
public record TodoResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        LocalDateTime createdAt
) {
}
