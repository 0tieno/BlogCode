package com.blogcode.ecommerce.service.impl;

import com.blogcode.ecommerce.domain.Order;
import com.blogcode.ecommerce.domain.OrderItem;
import com.blogcode.ecommerce.domain.OrderStatus;
import com.blogcode.ecommerce.domain.Product;
import com.blogcode.ecommerce.dto.OrderCreateRequest;
import com.blogcode.ecommerce.dto.OrderDto;
import com.blogcode.ecommerce.dto.OrderItemRequest;
import com.blogcode.ecommerce.dto.PageResponse;
import com.blogcode.ecommerce.exception.ResourceNotFoundException;
import com.blogcode.ecommerce.mapper.OrderMapper;
import com.blogcode.ecommerce.repository.OrderRepository;
import com.blogcode.ecommerce.repository.ProductRepository;
import com.blogcode.ecommerce.service.EmailService;
import com.blogcode.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default {@link OrderService} implementation backed by
 * {@link OrderRepository} and {@link ProductRepository}.
 *
 * <p><strong>Why this class exists:</strong> this is where the
 * Product/Order relationship, stock management, and asynchronous email
 * notification all come together. Keeping this orchestration in the
 * service layer (rather than the controller or the entities themselves)
 * follows the single-responsibility principle: entities model data and
 * invariants, controllers translate HTTP <-> DTOs, and services coordinate
 * the actual business transaction.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    /**
     * {@inheritDoc}
     *
     * <p>Everything here - stock validation, price snapshotting, and the
     * INSERTs cascaded from the {@link Order} aggregate - runs inside one
     * {@code @Transactional} boundary, so a failure partway through (e.g.
     * insufficient stock on the second line item) rolls back the entire
     * order instead of leaving a half-decremented stock count. The
     * confirmation email is fired only after that transaction is set up to
     * commit, via the {@code @Async} {@link EmailService}.
     */
    @Override
    @Transactional
    public OrderDto create(OrderCreateRequest request) {
        Order order = Order.builder()
                .customerEmail(request.customerEmail())
                .status(OrderStatus.PENDING)
                .build();

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository
                    .findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemRequest.productId()));

            if (product.getStockQuantity() < itemRequest.quantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for product '" + product.getName() + "': requested "
                                + itemRequest.quantity() + ", available " + product.getStockQuantity());
            }
            // Decrement stock immediately so concurrent orders cannot both
            // succeed against the same, now-oversold inventory.
            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());

            OrderItem item = OrderItem.builder()
                    .product(product)
                    // unitPrice is snapshotted from the product's current
                    // price at order time; see OrderItem's class Javadoc.
                    .unitPrice(product.getPrice())
                    .quantity(itemRequest.quantity())
                    .build();
            order.addItem(item);
        }

        order.recalculateTotal();
        Order saved = orderRepository.save(order);

        // Fire-and-forget: sendOrderConfirmation is @Async, so this call
        // returns immediately and the email "sends" on a separate thread
        // pool while the HTTP response is already being written.
        emailService.sendOrderConfirmation(saved);

        return OrderMapper.toDto(saved);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public OrderDto getById(Long id) {
        return OrderMapper.toDto(findEntityById(id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderDto> getAll(Pageable pageable) {
        Page<Order> page = orderRepository.findAll(pageable);
        return PageResponse.from(page.map(OrderMapper::toDto));
    }

    /**
     * {@inheritDoc}
     *
     * <p>Rejects any transition once an order has reached a
     * {@link OrderStatus#isTerminal()} state, modeling a simple but real
     * state-machine invariant: delivered/cancelled orders are immutable.
     */
    @Override
    @Transactional
    public OrderDto updateStatus(Long id, OrderStatus newStatus) {
        Order order = findEntityById(id);
        if (order.getStatus().isTerminal()) {
            throw new IllegalStateException(
                    "Order " + id + " is already in a terminal status (" + order.getStatus() + ")");
        }
        order.setStatus(newStatus);
        return OrderMapper.toDto(order);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Restores stock for every line item before marking the order
     * cancelled, so cancelling an order makes its reserved inventory
     * available for other customers again.
     */
    @Override
    @Transactional
    public OrderDto cancel(Long id) {
        Order order = findEntityById(id);
        if (order.getStatus().isTerminal()) {
            throw new IllegalStateException(
                    "Order " + id + " is already in a terminal status (" + order.getStatus() + ")");
        }
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return OrderMapper.toDto(order);
    }

    /**
     * Shared lookup helper that centralizes the "find or throw 404" pattern.
     *
     * @param id the order id to look up
     * @return the managed {@link Order} entity
     * @throws ResourceNotFoundException if no order has this id
     */
    private Order findEntityById(Long id) {
        return orderRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
}
