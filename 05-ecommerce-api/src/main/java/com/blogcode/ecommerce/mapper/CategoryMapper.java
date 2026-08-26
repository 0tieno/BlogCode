package com.blogcode.ecommerce.mapper;

import com.blogcode.ecommerce.domain.Category;
import com.blogcode.ecommerce.dto.CategoryDto;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Stateless mapper between the {@link Category} JPA entity and its DTO
 * representations.
 *
 * <p><strong>Why this class exists:</strong> keeping entity/DTO translation
 * in small, dedicated, static-method classes (instead of scattering
 * {@code new CategoryDto(...)} calls across every service) makes the
 * mapping logic easy to find, easy to unit test in isolation, and easy to
 * swap for a library like MapStruct later without touching service code.
 */
public final class CategoryMapper {

    private CategoryMapper() {
        // Utility class: only static mapping methods, never instantiated.
    }

    /**
     * Converts a {@link Category} entity into its read-model {@link CategoryDto}.
     *
     * @param category the entity to convert; must not be {@code null}
     * @return an equivalent, detached {@link CategoryDto}
     */
    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName(), category.getDescription());
    }

    /**
     * Converts a set of {@link Category} entities into a set of
     * {@link CategoryDto}s, used when mapping a product's categories.
     *
     * @param categories the entities to convert
     * @return an equivalent set of {@link CategoryDto}s
     */
    public static Set<CategoryDto> toDtoSet(Set<Category> categories) {
        return categories.stream().map(CategoryMapper::toDto).collect(Collectors.toSet());
    }
}
