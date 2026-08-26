package com.blogcode.ecommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

/**
 * Read-model DTO returned to API clients whenever a {@code Product} is
 * exposed over HTTP. See {@link CategoryDto} for the rationale behind using
 * a dedicated DTO (and a {@code record}) instead of serializing the JPA
 * entity directly.
 *
 * @param id            database identifier of the product
 * @param name          product title
 * @param description   longer free-text description
 * @param price         unit price
 * @param stockQuantity units currently available to sell
 * @param imageUrl      public URL of the product image, or {@code null}
 * @param categories    flattened set of categories this product belongs to
 * @param createdAt     when the product was first created
 * @param updatedAt     when the product was last modified
 */
public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        Set<CategoryDto> categories,
        Instant createdAt,
        Instant updatedAt) {
}
