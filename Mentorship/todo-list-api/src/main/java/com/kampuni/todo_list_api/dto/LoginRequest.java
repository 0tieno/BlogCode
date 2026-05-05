package com.kampuni.todo_list_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// ✅ FIXED: Added validation annotations — never trust user input.
public record LoginRequest(
        @NotBlank(message = "Email is required") @Email(message = "Enter a valid email") String email,
        @NotBlank(message = "Password is required") String password
) {
}
