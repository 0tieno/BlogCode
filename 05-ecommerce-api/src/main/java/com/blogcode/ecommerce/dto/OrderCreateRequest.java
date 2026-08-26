package com.blogcode.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Write-model DTO accepted by {@code POST /api/v1/orders} to place a new
 * order for one or more products.
 *
 * <p>{@code @Valid} on the nested {@code items} list ensures Bean Validation
 * recurses into each {@link OrderItemRequest}, so a request with a
 * zero-quantity line item is rejected with a clear 400 response instead of
 * failing deep inside the service layer.
 *
 * @param customerEmail email address of the customer placing the order
 * @param items         one or more requested product/quantity pairs
 */
public record OrderCreateRequest(

        @NotBlank(message = "customerEmail is required")
        @Email(message = "customerEmail must be a valid email address")
        String customerEmail,

        @NotEmpty(message = "An order must contain at least one item")
        List<@Valid OrderItemRequest> items) {
}
