package com.example.order_management.domain;

import com.example.order_management.domain.exception.InvalidOrderStateException;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCreateOrderWithAtLeastOneItemAndCalculateTotal() {
        UUID customerId = UUID.randomUUID();
        OrderItem item1 = OrderItem.of(UUID.randomUUID(), 2, Money.of(new BigDecimal("5.00"), "USD"));
        OrderItem item2 = OrderItem.of(UUID.randomUUID(), 1, Money.of(new BigDecimal("10.00"), "USD"));

        Order order = Order.create(customerId, List.of(item1, item2));

        assertThat(order.getId()).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("20.00");
    }

    @Test
    void shouldRejectOrderWithoutItems() {
        UUID customerId = UUID.randomUUID();

        assertThatThrownBy(() -> Order.create(customerId, List.of()))
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void shouldNotAllowPlacingOrderBelowMinimumValue() {
        UUID customerId = UUID.randomUUID();
        OrderItem item = OrderItem.of(UUID.randomUUID(), 1, Money.of(new BigDecimal("5.00"), "USD"));
        Order order = Order.create(customerId, List.of(item));

        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void shouldAllowPlacingOrderAboveMinimumValue() {
        UUID customerId = UUID.randomUUID();
        OrderItem item = OrderItem.of(UUID.randomUUID(), 2, Money.of(new BigDecimal("5.00"), "USD"));
        Order order = Order.create(customerId, List.of(item));

        order.markAsPaid();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void shouldAllowCancellationFromPendingOrPaid() {
        UUID customerId = UUID.randomUUID();
        OrderItem item = OrderItem.of(UUID.randomUUID(), 2, Money.of(new BigDecimal("5.00"), "USD"));
        Order order = Order.create(customerId, List.of(item));

        order.cancel();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        Order paidOrder = Order.create(customerId, List.of(item));
        paidOrder.markAsPaid();
        paidOrder.cancel();
        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldNotAllowCancellationFromShipped() {
        UUID customerId = UUID.randomUUID();
        OrderItem item = OrderItem.of(UUID.randomUUID(), 2, Money.of(new BigDecimal("10.00"), "USD"));
        Order order = Order.create(customerId, List.of(item));
        order.markAsPaid();
        order.markAsShipped();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void shouldAllowShippingOnlyFromPaid() {
        UUID customerId = UUID.randomUUID();
        OrderItem item = OrderItem.of(UUID.randomUUID(), 2, Money.of(new BigDecimal("10.00"), "USD"));
        Order order = Order.create(customerId, List.of(item));

        assertThatThrownBy(order::markAsShipped)
                .isInstanceOf(InvalidOrderStateException.class);

        order.markAsPaid();
        order.markAsShipped();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }
}

