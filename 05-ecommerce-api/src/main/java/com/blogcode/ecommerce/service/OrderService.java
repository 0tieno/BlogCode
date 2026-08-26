package com.blogcode.ecommerce.service;

import com.blogcode.ecommerce.domain.OrderStatus;
import com.blogcode.ecommerce.dto.OrderCreateRequest;
import com.blogcode.ecommerce.dto.OrderDto;
import com.blogcode.ecommerce.dto.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Business-logic contract for placing and managing orders. See
 * {@link CategoryService} for the rationale behind exposing a service
 * interface separate from its implementation.
 */
public interface OrderService {

    /**
     * Places a new order: validates stock, snapshots unit prices, persists
     * the {@code Order} aggregate, decrements stock, and triggers an
     * asynchronous order-confirmation email.
     *
     * @param request validated customer email and requested line items
     * @return the created order as a read-model DTO
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if any referenced product does not exist
     * @throws IllegalStateException if a product does not have enough stock
     */
    OrderDto create(OrderCreateRequest request);

    /**
     * Retrieves a single order by id, including its line items.
     *
     * @param id the order id
     * @return the matching order as a read-model DTO
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if no order has this id
     */
    OrderDto getById(Long id);

    /**
     * Retrieves a page of orders, most recent first.
     *
     * @param pageable paging and sorting instructions from the client
     * @return a page of orders wrapped in {@link PageResponse}
     */
    PageResponse<OrderDto> getAll(Pageable pageable);

    /**
     * Transitions an order to a new status (e.g. CONFIRMED, SHIPPED).
     *
     * @param id        the order id to update
     * @param newStatus the target status
     * @return the updated order as a read-model DTO
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if no order has this id
     * @throws IllegalStateException if the order is already in a terminal status
     */
    OrderDto updateStatus(Long id, OrderStatus newStatus);

    /**
     * Cancels an order, restoring the reserved stock back to each ordered
     * product.
     *
     * @param id the order id to cancel
     * @return the cancelled order as a read-model DTO
     * @throws com.blogcode.ecommerce.exception.ResourceNotFoundException if no order has this id
     * @throws IllegalStateException if the order is already in a terminal status
     */
    OrderDto cancel(Long id);
}
