package com.blogcode.ecommerce.mapper;

import com.blogcode.ecommerce.domain.Product;
import com.blogcode.ecommerce.dto.ProductDto;

/**
 * Stateless mapper between the {@link Product} JPA entity and its DTO
 * representations. See {@link CategoryMapper} for the rationale behind this
 * pattern.
 */
public final class ProductMapper {

    private ProductMapper() {
        // Utility class: only static mapping methods, never instantiated.
    }

    /**
     * Converts a {@link Product} entity into its read-model {@link ProductDto},
     * including a flattened set of its categories.
     *
     * <p>Because {@code Product.categories} is lazily fetched, calling this
     * method outside of an active persistence context/transaction would
     * throw a {@code LazyInitializationException}. Service methods are
     * annotated {@code @Transactional} precisely so this mapping can safely
     * touch lazy collections before the session closes.
     *
     * @param product the entity to convert; must not be {@code null}
     * @return an equivalent, detached {@link ProductDto}
     */
    public static ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                CategoryMapper.toDtoSet(product.getCategories()),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}
