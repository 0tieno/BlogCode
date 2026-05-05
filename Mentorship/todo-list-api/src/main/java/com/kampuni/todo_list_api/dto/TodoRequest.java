package com.kampuni.todo_list_api.dto;

import jakarta.validation.constraints.NotBlank;

// ✅ FIXED: Added validation — a todo without a title makes no sense.
public record TodoRequest(
        @NotBlank(message = "Title is required") String title,
        String description   // description is optional, so no @NotBlank needed
) {
}
