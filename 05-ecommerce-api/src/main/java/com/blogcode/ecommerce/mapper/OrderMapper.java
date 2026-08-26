package com.blogcode.ecommerce.mapper;

import com.blogcode.ecommerce.domain.Order;
import com.blogcode.ecommerce.domain.OrderItem;
import com.blogcode.ecommerce.dto.OrderDto;
import com.blogcode.ecommerce.dto.OrderItemDto;

/**
 * Stateless mapper between the {@link Order}/{@link OrderItem} JPA entities
 * and their DTO representations. See {@link CategoryMapper} for the
 * rationale behind this pattern.
 */
public final class OrderMapper {

    private OrderMapper() {
        // Utility class: only static mapping methods, never instantiated.
    }

    /**
     * Converts a single {@link OrderItem} into its {@link OrderItemDto},
     * denormalizing the product name for convenient client-side rendering.
     *
     * @param item the entity to convert; must not be {@code null}
     * @return an equivalent, detached {@link OrderItemDto}
     */
    public static OrderItemDto toDto(OrderItem item) {
        return new OrderItemDto(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal());
    }

    /**
     * Converts an {@link Order} aggregate, including every line item, into
     * its {@link OrderDto} representation.
     *
     * @param order the aggregate root to convert; must not be {@code null}
     * @return an equivalent, detached {@link OrderDto}
     */
    public static OrderDto toDto(Order order) {
        return new OrderDto(
                order.getId(),
                order.getCustomerEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream().map(OrderMapper::toDto).toList(),
                order.getCreatedAt(),
                order.getUpdatedAt());
    }
}
