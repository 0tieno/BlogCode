package com.blogcode.ecommerce.dto;

import java.math.BigDecimal;

/**
 * Read-model DTO for a single {@code OrderItem} line, nested inside
 * {@link OrderDto}.
 *
 * @param id           database identifier of the order item
 * @param productId    identifier of the purchased product
 * @param productName  denormalized product name, convenient for clients
 *                     that only render an order summary and should not need
 *                     to make a second call to the products endpoint
 * @param quantity     number of units purchased
 * @param unitPrice    price per unit at the time of purchase
 * @param subtotal     {@code unitPrice * quantity}, precomputed for clients
 */
public record OrderItemDto(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal) {
}
