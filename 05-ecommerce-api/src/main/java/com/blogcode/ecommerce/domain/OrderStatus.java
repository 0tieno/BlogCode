package com.blogcode.ecommerce.domain;

/**
 * Enumerates the lifecycle states of an {@link Order}.
 *
 * <p><strong>Why this class exists:</strong> using a Java {@code enum}
 * instead of a raw {@code String} status column gives us compile-time
 * safety (no typos like {@code "SHIPED"}), a fixed, self-documenting set of
 * valid values, and lets {@link OrderStatus#isTerminal()} express business
 * rules (such as "cancelled orders cannot transition further") in one place
 * instead of scattering string comparisons across the codebase.
 */
public enum OrderStatus {

    /** Order has been created and is awaiting payment/processing. */
    PENDING,

    /** Payment succeeded and the order is being prepared for shipment. */
    CONFIRMED,

    /** The order has left the warehouse. */
    SHIPPED,

    /** The order has reached the customer. */
    DELIVERED,

    /** The order was cancelled before completion. */
    CANCELLED;

    /**
     * Reports whether this status is a terminal state, i.e. one from which
     * the order can no longer transition to any other status.
     *
     * @return true if this status is {@link #DELIVERED} or {@link #CANCELLED}
     */
    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }
}
