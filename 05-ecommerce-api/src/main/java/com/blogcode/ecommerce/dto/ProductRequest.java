package com.blogcode.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;

/**
 * Write-model DTO accepted by the API when creating or updating a product.
 * See {@link CategoryRequest} for the rationale behind splitting read and
 * write DTOs.
 *
 * @param name           required product title (max 200 characters)
 * @param description    optional free-text description (max 2000 characters)
 * @param price           required unit price, must be zero or positive
 * @param stockQuantity  required stock count, must be zero or positive
 * @param imageUrl       optional image URL, typically produced by the image
 *                       upload endpoint before this request is sent
 * @param categoryIds    ids of existing {@code Category} rows to associate
 *                       with the product; may be empty but not null
 */
public record ProductRequest(

        @NotBlank(message = "Product name is required")
        @Size(max = 200, message = "Product name must be at most 200 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must not be negative")
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock quantity must not be negative")
        Integer stockQuantity,

        String imageUrl,

        @NotNull(message = "categoryIds must be provided (an empty set is allowed)")
        Set<Long> categoryIds) {
}
