package com.blogcode.ecommerce.controller;

import com.blogcode.ecommerce.domain.OrderStatus;
import com.blogcode.ecommerce.dto.OrderCreateRequest;
import com.blogcode.ecommerce.dto.OrderDto;
import com.blogcode.ecommerce.dto.PageResponse;
import com.blogcode.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing order-placement and order-management endpoints.
 *
 * <p><strong>Why this class exists:</strong> see {@link CategoryController}
 * for the general rationale behind keeping controllers thin. Placing an
 * order is the most business-rule-heavy operation in this module (stock
 * checks, price snapshotting, async email), all of which lives in
 * {@link OrderService} - this controller only handles HTTP concerns.
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Places a new order.
     *
     * @param request validated customer email and requested line items
     * @return {@code 201 Created} with the new order in the response body
     */
    @PostMapping
    public ResponseEntity<OrderDto> create(@Valid @RequestBody OrderCreateRequest request) {
        OrderDto created = orderService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Lists orders with pagination, most recently created first by default.
     *
     * @param pageable paging/sorting parameters bound automatically by Spring Data
     * @return {@code 200 OK} with a page of orders
     */
    @GetMapping
    public ResponseEntity<PageResponse<OrderDto>> getAll(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(orderService.getAll(pageable));
    }

    /**
     * Retrieves a single order by id, including its line items.
     *
     * @param id the order id, taken from the URL path
     * @return {@code 200 OK} with the matching order
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    /**
     * Transitions an order to a new status, e.g. {@code CONFIRMED} or
     * {@code SHIPPED}.
     *
     * <p>Example: {@code PATCH /api/v1/orders/1/status?status=SHIPPED}
     *
     * @param id     the order id, taken from the URL path
     * @param status the target {@link OrderStatus}, bound from a query parameter
     * @return {@code 200 OK} with the updated order
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto> updateStatus(
            @PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    /**
     * Cancels an order, restoring stock for each of its line items.
     *
     * @param id the order id, taken from the URL path
     * @return {@code 200 OK} with the cancelled order
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderDto> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}
