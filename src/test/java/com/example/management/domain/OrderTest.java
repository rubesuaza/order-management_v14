package com.example.management.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void createOrderShouldRequireCustomer() {
        assertThrows(IllegalArgumentException.class, () -> Order.create(null));
    }

    @Test
    void shouldNotAllowLinesWithNonPositiveQuantity() {
        Order order = Order.create("CUST-1");

        assertThrows(IllegalArgumentException.class,
                () -> order.addLine("PROD-1", new BigDecimal("10.00"), 0));

        assertThrows(IllegalArgumentException.class,
                () -> order.addLine("PROD-1", new BigDecimal("10.00"), -1));
    }

    @Test
    void shouldNotAllowLinesWithNegativePrice() {
        Order order = Order.create("CUST-1");

        assertThrows(IllegalArgumentException.class,
                () -> order.addLine("PROD-1", new BigDecimal("-1.00"), 1));
    }

    @Test
    void orderTotalShouldBeSumOfLines() {
        Order order = Order.create("CUST-1");

        order.addLine("PROD-1", new BigDecimal("10.00"), 2); // 20.00
        order.addLine("PROD-2", new BigDecimal("5.50"), 3);  // 16.50

        assertEquals(new BigDecimal("36.50"), order.getTotal());
    }

    @Test
    void shouldNotAllowModifyingConfirmedOrder() {
        Order order = Order.create("CUST-1");
        order.addLine("PROD-1", new BigDecimal("10.00"), 1);
        order.confirm();

        assertThrows(IllegalStateException.class,
                () -> order.addLine("PROD-2", new BigDecimal("5.00"), 1));
    }

    @Test
    void shouldNotAllowConfirmingOrderWithoutLines() {
        Order order = Order.create("CUST-1");

        assertThrows(IllegalStateException.class, order::confirm);
    }

    @Test
    void shouldNotAllowCancelingAlreadyCanceledOrder() {
        Order order = Order.create("CUST-1");
        order.addLine("PROD-1", new BigDecimal("10.00"), 1);
        order.cancel();

        assertThrows(IllegalStateException.class, order::cancel);
    }

    @Test
    void canConfirmAndTransitionToConfirmedState() {
        Order order = Order.create("CUST-1");
        order.addLine("PROD-1", new BigDecimal("10.00"), 1);

        order.confirm();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    void shouldNotAllowModifyingCanceledOrder() {
        Order order = Order.create("CUST-1");
        order.addLine("PROD-1", new BigDecimal("10.00"), 1);
        order.cancel();

        assertThrows(IllegalStateException.class,
                () -> order.addLine("PROD-2", new BigDecimal("5.00"), 1));
    }

    @Test
    void canCancelConfirmedOrderAndTransitionToCanceledState() {
        Order order = Order.create("CUST-1");
        order.addLine("PROD-1", new BigDecimal("10.00"), 1);
        order.confirm();

        order.cancel();

        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }
}

