package com.blogcode.ecommerce.controller;

import com.blogcode.ecommerce.dto.PageResponse;
import com.blogcode.ecommerce.dto.ProductDto;
import com.blogcode.ecommerce.dto.ProductRequest;
import com.blogcode.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing CRUD, pagination and filtering endpoints for
 * products.
 *
 * <p><strong>Why this class exists:</strong> see {@link CategoryController}
 * for the general rationale behind keeping controllers thin. This
 * controller additionally demonstrates how Spring MVC binds query
 * parameters directly into a {@link Pageable} (for paging/sorting) and into
 * plain {@code @RequestParam} arguments (for filtering), both of which are
 * simply forwarded to {@link ProductService#search}.
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Creates a new product.
     *
     * @param request validated product payload
     * @return {@code 201 Created} with the new product in the response body
     */
    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductRequest request) {
        ProductDto created = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Searches products with optional filters and pagination.
     *
     * <p>Example: {@code GET /api/v1/products?name=phone&minPrice=100&page=0&size=10&sort=price,asc}
     *
     * @param name       optional case-insensitive name fragment filter
     * @param categoryId optional category id filter
     * @param minPrice   optional inclusive minimum price filter
     * @param maxPrice   optional inclusive maximum price filter
     * @param pageable   paging/sorting parameters bound automatically by
     *                   Spring Data from {@code page}, {@code size} and
     *                   {@code sort} query parameters; defaults to 20 items
     *                   per page sorted by id when not supplied
     * @return {@code 200 OK} with a page of matching products
     */
    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(productService.search(name, categoryId, minPrice, maxPrice, pageable));
    }

    /**
     * Retrieves a single product by id. Backed by the Redis product cache;
     * see {@code ProductServiceImpl.getById}.
     *
     * @param id the product id, taken from the URL path
     * @return {@code 200 OK} with the matching product
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    /**
     * Updates an existing product.
     *
     * @param id      the product id, taken from the URL path
     * @param request validated new product payload
     * @return {@code 200 OK} with the updated product
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    /**
     * Deletes a product by id.
     *
     * @param id the product id, taken from the URL path
     * @return {@code 204 No Content} on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
