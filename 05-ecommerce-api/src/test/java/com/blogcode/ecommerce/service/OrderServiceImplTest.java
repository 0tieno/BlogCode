package com.blogcode.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blogcode.ecommerce.domain.Order;
import com.blogcode.ecommerce.domain.Product;
import com.blogcode.ecommerce.dto.OrderCreateRequest;
import com.blogcode.ecommerce.dto.OrderDto;
import com.blogcode.ecommerce.dto.OrderItemRequest;
import com.blogcode.ecommerce.exception.ResourceNotFoundException;
import com.blogcode.ecommerce.repository.OrderRepository;
import com.blogcode.ecommerce.repository.ProductRepository;
import com.blogcode.ecommerce.service.impl.OrderServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * Plain Mockito-based unit test for {@link OrderServiceImpl}, exercising
 * the core business rules without booting any Spring context at all.
 *
 * <p><strong>Why this class exists:</strong> unlike {@code ProductControllerTest}
 * (a Spring web slice test), this test proves that a well-designed service
 * layer - built around interfaces and constructor injection
 * ({@code @RequiredArgsConstructor}) - can be unit tested with plain "new
 * OrderServiceImpl(mock, mock, mock)" wiring. No Spring context, no
 * database, no Redis: just fast, focused business-rule verification.
 */
class OrderServiceImplTest {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private EmailService emailService;
    private OrderService orderService;

    /**
     * Rebuilds fresh mocks and a fresh {@link OrderServiceImpl} before every
     * test method so state never leaks between test cases.
     */
    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        productRepository = mock(ProductRepository.class);
        emailService = mock(EmailService.class);
        orderService = new OrderServiceImpl(orderRepository, productRepository, emailService);
    }

    /**
     * Verifies that placing an order with sufficient stock decrements the
     * product's stock, persists the order, and triggers the (mocked)
     * confirmation email.
     */
    @Test
    void create_decrementsStockAndSendsConfirmationEmail_whenStockIsSufficient() {
        Product keyboard = Product.builder()
                .id(1L)
                .name("Mechanical Keyboard")
                .price(new BigDecimal("100.00"))
                .stockQuantity(5)
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(keyboard));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(99L);
            return order;
        });
        doNothing().when(emailService).sendOrderConfirmation(any(Order.class));

        OrderCreateRequest request =
                new OrderCreateRequest("shopper@example.com", List.of(new OrderItemRequest(1L, 2)));

        OrderDto result = orderService.create(request);

        assertThat(result.id()).isEqualTo(99L);
        assertThat(result.totalAmount()).isEqualByComparingTo("200.00");
        assertThat(keyboard.getStockQuantity()).isEqualTo(3);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getItems()).hasSize(1);
        verify(emailService).sendOrderConfirmation(any(Order.class));
    }

    /**
     * Verifies that placing an order for more units than are in stock
     * fails fast with {@link IllegalStateException} and never persists an
     * order or sends a confirmation email.
     */
    @Test
    void create_throwsIllegalStateException_whenStockIsInsufficient() {
        Product keyboard = Product.builder()
                .id(1L)
                .name("Mechanical Keyboard")
                .price(new BigDecimal("100.00"))
                .stockQuantity(1)
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(keyboard));

        OrderCreateRequest request =
                new OrderCreateRequest("shopper@example.com", List.of(new OrderItemRequest(1L, 5)));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");

        verify(orderRepository, never()).save(Mockito.any());
        verify(emailService, never()).sendOrderConfirmation(Mockito.any());
    }

    /**
     * Verifies that requesting an order containing a non-existent product
     * fails with {@link ResourceNotFoundException} instead of a raw
     * {@code NullPointerException}.
     */
    @Test
    void create_throwsResourceNotFoundException_whenProductDoesNotExist() {
        when(productRepository.findById(42L)).thenReturn(Optional.empty());

        OrderCreateRequest request =
                new OrderCreateRequest("shopper@example.com", List.of(new OrderItemRequest(42L, 1)));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("42");
    }
}
