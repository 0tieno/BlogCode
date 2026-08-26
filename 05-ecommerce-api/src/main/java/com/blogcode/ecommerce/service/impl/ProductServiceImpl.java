package com.blogcode.ecommerce.service.impl;

import com.blogcode.ecommerce.domain.Category;
import com.blogcode.ecommerce.domain.Product;
import com.blogcode.ecommerce.dto.PageResponse;
import com.blogcode.ecommerce.dto.ProductDto;
import com.blogcode.ecommerce.dto.ProductRequest;
import com.blogcode.ecommerce.exception.ResourceNotFoundException;
import com.blogcode.ecommerce.mapper.ProductMapper;
import com.blogcode.ecommerce.repository.CategoryRepository;
import com.blogcode.ecommerce.repository.ProductRepository;
import com.blogcode.ecommerce.service.ProductService;
import com.blogcode.ecommerce.specification.ProductSpecifications;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link ProductService} implementation backed by
 * {@link ProductRepository}, demonstrating Redis-backed caching and dynamic
 * JPA Specification filtering.
 *
 * <p><strong>Why this class exists:</strong> product reads vastly
 * outnumber product writes in a typical storefront, which makes the
 * product catalog an ideal candidate for caching. This class shows the two
 * halves of that pattern together: {@code @Cacheable} to serve repeated
 * reads from Redis instead of PostgreSQL, and {@code @CacheEvict} to
 * invalidate the cache the moment the underlying data changes, preventing
 * customers from seeing stale prices or stock counts.
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    /** Name of the Redis cache region holding individual product lookups. */
    public static final String PRODUCT_CACHE = "products";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    /**
     * {@inheritDoc}
     *
     * <p>No cache eviction is required here because creating a product
     * cannot possibly invalidate an existing cache entry (its id does not
     * exist yet), but the product list/search results a client already has
     * cached client-side would naturally be refreshed on their next request.
     */
    @Override
    @Transactional
    public ProductDto create(ProductRequest request) {
        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .imageUrl(request.imageUrl())
                .build();
        for (Category category : resolveCategories(request.categoryIds())) {
            product.addCategory(category);
        }
        Product saved = productRepository.save(product);
        return ProductMapper.toDto(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>{@code @Cacheable} makes Spring check the {@value #PRODUCT_CACHE}
     * Redis cache region (keyed by the product id, the method's only
     * argument) before running this method body at all. On a cache hit,
     * the method body - and therefore the PostgreSQL query - never runs.
     */
    @Override
    @Cacheable(cacheNames = PRODUCT_CACHE, key = "#id")
    @Transactional(readOnly = true)
    public ProductDto getById(Long id) {
        Product product = findEntityById(id);
        return ProductMapper.toDto(product);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Search results are intentionally <em>not</em> cached: the
     * combination of filters and paging parameters is unbounded, which
     * would make the cache grow without limit and rarely produce hits.
     * Caching is reserved for the high-traffic, low-cardinality
     * {@link #getById(Long)} lookup instead.
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductDto> search(
            String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        List<Specification<Product>> predicates = new ArrayList<>();
        addIfPresent(predicates, ProductSpecifications.hasNameContaining(name));
        addIfPresent(predicates, ProductSpecifications.hasCategoryId(categoryId));
        addIfPresent(predicates, ProductSpecifications.hasPriceGreaterThanOrEqualTo(minPrice));
        addIfPresent(predicates, ProductSpecifications.hasPriceLessThanOrEqualTo(maxPrice));

        Specification<Product> combined = Specification.allOf(predicates);
        Page<Product> page = productRepository.findAll(combined, pageable);
        return PageResponse.from(page.map(ProductMapper::toDto));
    }

    /**
     * {@inheritDoc}
     *
     * <p>{@code @CacheEvict} removes the stale entry for this product id
     * from the {@value #PRODUCT_CACHE} region the moment the update
     * commits, guaranteeing the next {@link #getById(Long)} call re-reads
     * fresh data from PostgreSQL and repopulates the cache.
     */
    @Override
    @CacheEvict(cacheNames = PRODUCT_CACHE, key = "#id")
    @Transactional
    public ProductDto update(Long id, ProductRequest request) {
        Product product = findEntityById(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setImageUrl(request.imageUrl());

        // Replace the category associations wholesale rather than trying to
        // diff old vs. new sets - simpler to reason about for a teaching
        // codebase, and addCategory() keeps both sides of the relationship
        // (Product <-> Category) consistent.
        for (Category existing : Set.copyOf(product.getCategories())) {
            product.removeCategory(existing);
        }
        for (Category category : resolveCategories(request.categoryIds())) {
            product.addCategory(category);
        }
        return ProductMapper.toDto(product);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Also evicts the cache entry so a deleted product is never served
     * from a stale Redis entry after the row is gone from PostgreSQL.
     */
    @Override
    @CacheEvict(cacheNames = PRODUCT_CACHE, key = "#id")
    @Transactional
    public void delete(Long id) {
        Product product = findEntityById(id);
        productRepository.delete(product);
    }

    /**
     * Shared lookup helper that centralizes the "find or throw 404" pattern.
     *
     * @param id the product id to look up
     * @return the managed {@link Product} entity
     * @throws ResourceNotFoundException if no product has this id
     */
    private Product findEntityById(Long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    /**
     * Resolves a set of category ids into their managed {@link Category}
     * entities, failing fast if any id does not exist.
     *
     * @param categoryIds ids supplied by the client
     * @return the corresponding managed {@link Category} entities
     * @throws ResourceNotFoundException if any id does not correspond to a category
     */
    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        Set<Category> categories = new HashSet<>();
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository
                    .findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + categoryId));
            categories.add(category);
        }
        return categories;
    }

    /**
     * Adds a {@link Specification} to the working list only if it is
     * non-null, i.e. only if the caller actually supplied that filter. See
     * {@link ProductSpecifications} for why each factory method may return
     * {@code null}.
     *
     * @param predicates the mutable list being assembled
     * @param spec       the specification to add, or {@code null} to skip
     */
    private static void addIfPresent(List<Specification<Product>> predicates, Specification<Product> spec) {
        if (spec != null) {
            predicates.add(spec);
        }
    }
}
