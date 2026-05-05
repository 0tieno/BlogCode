package com.learn.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Input Validation
//
// Never trust data coming from the client. You must VALIDATE it.
// Spring's validation annotations describe RULES for each field.
// If any rule is broken, Spring automatically rejects the request with 400 Bad Request.
//
// To activate validation on a controller method, add @Valid to the @RequestBody parameter.
// ══════════════════════════════════════════════════════════════════════════════

// Record = immutable DTO. Perfect for request bodies.
// The client sends: { "title": "Buy milk", "description": "2 litres" }
public record TodoRequest(

        // @NotBlank = not null AND not empty AND not just whitespace
        // message = what the error response will say
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        // description is optional — no @NotBlank
        String description
) {}

