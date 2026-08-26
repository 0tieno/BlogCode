package com.blogcode.ecommerce.repository;

import com.blogcode.ecommerce.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Order} entities.
 *
 * <p><strong>Why this class exists:</strong> since {@link Order} is the
 * aggregate root that cascades saves/deletes to its {@code OrderItem}
 * children (see {@code Order.items}), a single repository here is enough to
 * manage the entire order aggregate - there is intentionally no separate
 * {@code OrderItemRepository}, reinforcing the aggregate-root pattern
 * taught by this module.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
