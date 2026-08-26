package com.blogcode.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Write-model DTO for a single requested line item, nested inside
 * {@link OrderCreateRequest}.
 *
 * @param productId id of the {@code Product} being ordered
 * @param quantity  number of units requested, must be at least 1
 */
public record OrderItemRequest(

        @NotNull(message = "productId is required")
        Long productId,

        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be at least 1")
        Integer quantity) {
}
