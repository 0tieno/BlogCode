package com.blogcode.ecommerce.repository;

import com.blogcode.ecommerce.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Spring Data JPA repository for {@link Product} entities.
 *
 * <p><strong>Why this class exists:</strong> beyond basic CRUD from
 * {@link JpaRepository}, this repository also extends
 * {@link JpaSpecificationExecutor}, which unlocks the JPA Criteria API via
 * the {@link org.springframework.data.jpa.domain.Specification} type. That
 * is what powers the dynamic, combinable product filters (by name, price
 * range, and category) built in
 * {@link com.blogcode.ecommerce.specification.ProductSpecifications} -
 * filters whose combination cannot be known ahead of time simply cannot be
 * expressed as a fixed set of derived query methods.
 */
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
}
