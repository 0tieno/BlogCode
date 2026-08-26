package com.blogcode.ecommerce.controller;

import com.blogcode.ecommerce.dto.CategoryDto;
import com.blogcode.ecommerce.dto.CategoryRequest;
import com.blogcode.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing CRUD endpoints for product categories.
 *
 * <p><strong>Why this class exists:</strong> controllers are the thinnest
 * possible layer in this architecture - they translate HTTP verbs/paths
 * into {@link CategoryService} calls and translate the result back into
 * HTTP status codes, deliberately containing zero business logic
 * themselves. That separation is what lets the service layer be unit
 * tested completely independently of the web framework.
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new category.
     *
     * @param request validated category payload
     * @return {@code 201 Created} with the new category in the response body
     */
    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryRequest request) {
        CategoryDto created = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Lists every category.
     *
     * @return {@code 200 OK} with the full, unpaginated category list
     */
    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    /**
     * Retrieves a single category by id.
     *
     * @param id the category id, taken from the URL path
     * @return {@code 200 OK} with the matching category
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    /**
     * Updates an existing category.
     *
     * @param id      the category id, taken from the URL path
     * @param request validated new category payload
     * @return {@code 200 OK} with the updated category
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    /**
     * Deletes a category by id.
     *
     * @param id the category id, taken from the URL path
     * @return {@code 204 No Content} on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
