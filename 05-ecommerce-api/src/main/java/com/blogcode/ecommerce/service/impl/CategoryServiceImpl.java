package com.blogcode.ecommerce.service.impl;

import com.blogcode.ecommerce.domain.Category;
import com.blogcode.ecommerce.dto.CategoryDto;
import com.blogcode.ecommerce.dto.CategoryRequest;
import com.blogcode.ecommerce.exception.ResourceNotFoundException;
import com.blogcode.ecommerce.mapper.CategoryMapper;
import com.blogcode.ecommerce.repository.CategoryRepository;
import com.blogcode.ecommerce.service.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link CategoryService} implementation backed by
 * {@link CategoryRepository}.
 *
 * <p><strong>Why this class exists:</strong> it contains the actual
 * business rules for categories (uniqueness checks, not-found handling)
 * kept separate from both the HTTP layer ({@code CategoryController}) and
 * the persistence layer ({@code CategoryRepository}). This three-layer
 * split (controller / service / repository) is the layered architecture
 * this whole curriculum module is built around.
 *
 * <p>{@code @RequiredArgsConstructor} (Lombok) generates a constructor for
 * every {@code private final} field, which Spring then uses for
 * constructor injection - the recommended dependency injection style
 * because it makes required dependencies explicit and immutable.
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * {@inheritDoc}
     *
     * <p>Runs inside a single transaction so that if any downstream logic
     * were added later (e.g. auditing), it would either fully commit or
     * fully roll back together with the insert.
     */
    @Override
    @Transactional
    public CategoryDto create(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
        Category saved = categoryRepository.save(category);
        return CategoryMapper.toDto(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>{@code @Transactional(readOnly = true)} lets Hibernate skip dirty
     * checking for this method, a small but idiomatic performance
     * optimization for read paths.
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long id) {
        Category category = findEntityById(id);
        return CategoryMapper.toDto(category);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll().stream().map(CategoryMapper::toDto).toList();
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public CategoryDto update(Long id, CategoryRequest request) {
        Category category = findEntityById(id);
        category.setName(request.name());
        category.setDescription(request.description());
        // No explicit save() call is required: within an open transaction,
        // Hibernate's "dirty checking" detects the field changes on this
        // managed entity and automatically issues an UPDATE at flush time.
        return CategoryMapper.toDto(category);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(Long id) {
        Category category = findEntityById(id);
        categoryRepository.delete(category);
    }

    /**
     * Shared lookup helper that centralizes the "find or throw 404" pattern
     * used by every method above.
     *
     * @param id the category id to look up
     * @return the managed {@link Category} entity
     * @throws ResourceNotFoundException if no category has this id
     */
    private Category findEntityById(Long id) {
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}
