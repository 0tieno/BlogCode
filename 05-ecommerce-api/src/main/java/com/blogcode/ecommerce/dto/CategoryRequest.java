package com.blogcode.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Write-model DTO accepted by the API when creating or updating a category.
 *
 * <p><strong>Why this class exists:</strong> separating "create/update"
 * request shapes from "read" response shapes (see {@link CategoryDto}) lets
 * each side evolve independently and lets us attach Bean Validation
 * ({@code jakarta.validation}) constraints exactly where user input enters
 * the system, without polluting the read-model or the entity itself.
 *
 * @param name        required, unique category name (max 100 characters)
 * @param description optional free-text description (max 500 characters)
 */
public record CategoryRequest(

        @NotBlank(message = "Category name is required")
        @Size(max = 100, message = "Category name must be at most 100 characters")
        String name,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description) {
}
