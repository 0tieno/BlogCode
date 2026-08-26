package com.blogcode.ecommerce.specification;

import com.blogcode.ecommerce.domain.Category;
import com.blogcode.ecommerce.domain.Product;
import jakarta.persistence.criteria.Join;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;

/**
 * Factory of composable {@link Specification} predicates for {@link Product}
 * queries, built directly against the JPA Criteria API.
 *
 * <p><strong>Why this class exists:</strong> the product listing endpoint
 * accepts an open-ended combination of optional filters (name, category,
 * min/max price). Writing one derived-query method per combination would
 * require 2^n methods. {@link Specification} lets us build exactly one
 * predicate per filter and compose only the ones the caller actually
 * supplied with {@code Specification.allOf(...)}, which is a core "advanced
 * JPA" concept this curriculum module is meant to teach.
 */
public final class ProductSpecifications {

    private ProductSpecifications() {
        // Utility class: only static factory methods, never instantiated.
    }

    /**
     * Builds a predicate that matches products whose name contains the
     * given fragment, case-insensitively.
     *
     * @param name the fragment to search for, may be {@code null}/blank to
     *             indicate "no filter" (in which case {@code null} is
     *             returned and {@code Specification.allOf} ignores it)
     * @return a specification matching on product name, or {@code null}
     */
    public static Specification<Product> hasNameContaining(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String likePattern = "%" + name.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), likePattern);
    }

    /**
     * Builds a predicate that matches products priced at or above the given
     * minimum.
     *
     * @param minPrice the inclusive minimum price, or {@code null} for "no filter"
     * @return a specification matching on minimum price, or {@code null}
     */
    public static Specification<Product> hasPriceGreaterThanOrEqualTo(BigDecimal minPrice) {
        if (minPrice == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    /**
     * Builds a predicate that matches products priced at or below the given
     * maximum.
     *
     * @param maxPrice the inclusive maximum price, or {@code null} for "no filter"
     * @return a specification matching on maximum price, or {@code null}
     */
    public static Specification<Product> hasPriceLessThanOrEqualTo(BigDecimal maxPrice) {
        if (maxPrice == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    /**
     * Builds a predicate that matches products belonging to the given
     * category id, expressed as an explicit {@link Join} across the
     * many-to-many {@code product_category} join table.
     *
     * <p>{@code query.distinct(true)} is required here: joining a
     * many-to-many relationship can produce duplicate {@code Product} rows
     * (one per matching category row) which would otherwise leak into the
     * paginated result set.
     *
     * @param categoryId the category id to filter by, or {@code null} for "no filter"
     * @return a specification matching on category membership, or {@code null}
     */
    public static Specification<Product> hasCategoryId(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Product, Category> categories = root.join("categories");
            return cb.equal(categories.get("id"), categoryId);
        };
    }
}
