package com.blogcode.ecommerce.dto;

import com.blogcode.ecommerce.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Read-model DTO returned to API clients whenever an {@code Order} is
 * exposed over HTTP, including its nested line items.
 *
 * @param id             database identifier of the order
 * @param customerEmail  email address of the customer who placed the order
 * @param status         current lifecycle status of the order
 * @param totalAmount    sum of all line item subtotals
 * @param items          the order's line items
 * @param createdAt      when the order was placed
 * @param updatedAt      when the order was last modified
 */
public record OrderDto(
        Long id,
        String customerEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemDto> items,
        Instant createdAt,
        Instant updatedAt) {
}
