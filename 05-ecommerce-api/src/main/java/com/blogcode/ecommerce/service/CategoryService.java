package com.blogcode.ecommerce.service;

import com.blogcode.ecommerce.dto.CategoryDto;
import com.blogcode.ecommerce.dto.CategoryRequest;
import java.util.List;

/**
 * Business-logic contract for managing product categories.
 *
 * <p><strong>Why this class exists:</strong> controllers depend on this
 * interface rather than a concrete implementation class. This is the
 * "program to an interface, not an implementation" principle: it lets the
 * implementation evolve (or be swapped for a test double/mock) without
 * touching {@code CategoryController}, and it makes the available business
 * operations easy to read in one place.
 */
public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param request validated category data supplied by the client
     * @return the created category as a read-model DTO
     */
    CategoryDto create(CategoryRequest request);

    /**
     * Retrieves a single category by id.
     *
     * @param id the category id
     * @return the matching category as a read-model DTO
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if no category has this id
     */
    CategoryDto getById(Long id);

    /**
     * Retrieves every category, unpaginated since categories are expected
     * to be a small, mostly-static reference list.
     *
     * @return all categories as read-model DTOs
     */
    List<CategoryDto> getAll();

    /**
     * Updates an existing category's name/description.
     *
     * @param id      the category id to update
     * @param request validated new category data
     * @return the updated category as a read-model DTO
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if no category has this id
     */
    CategoryDto update(Long id, CategoryRequest request);

    /**
     * Deletes a category by id.
     *
     * @param id the category id to delete
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if no category has this id
     */
    void delete(Long id);
}
