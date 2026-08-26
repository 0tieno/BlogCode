package com.blogcode.ecommerce.service;

import com.blogcode.ecommerce.dto.PageResponse;
import com.blogcode.ecommerce.dto.ProductDto;
import com.blogcode.ecommerce.dto.ProductRequest;
import java.math.BigDecimal;
import org.springframework.data.domain.Pageable;

/**
 * Business-logic contract for managing products, including cached reads and
 * dynamic filtering. See {@link CategoryService} for the rationale behind
 * exposing a service interface separate from its implementation.
 */
public interface ProductService {

    /**
     * Creates a new product and associates it with zero or more existing
     * categories.
     *
     * @param request validated product data supplied by the client
     * @return the created product as a read-model DTO
     */
    ProductDto create(ProductRequest request);

    /**
     * Retrieves a single product by id. Implementations are expected to
     * cache this lookup (see {@code @Cacheable}) since product detail pages
     * are read far more often than products are written.
     *
     * @param id the product id
     * @return the matching product as a read-model DTO
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if no product has this id
     */
    ProductDto getById(Long id);

    /**
     * Retrieves a page of products, optionally filtered by name fragment,
     * category id, and/or price range. Any {@code null} filter argument is
     * treated as "no constraint" for that dimension.
     *
     * @param name       optional case-insensitive name fragment filter
     * @param categoryId optional category id filter
     * @param minPrice   optional inclusive minimum price filter
     * @param maxPrice   optional inclusive maximum price filter
     * @param pageable   paging and sorting instructions from the client
     * @return a page of matching products wrapped in {@link PageResponse}
     */
    PageResponse<ProductDto> search(
            String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Updates an existing product's fields and category associations.
     *
     * @param id      the product id to update
     * @param request validated new product data
     * @return the updated product as a read-model DTO
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if no product has this id
     */
    ProductDto update(Long id, ProductRequest request);

    /**
     * Deletes a product by id.
     *
     * @param id the product id to delete
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if no product has this id
     */
    void delete(Long id);
}
