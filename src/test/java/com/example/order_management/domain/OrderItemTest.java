package com.example.order_management.domain;

import com.example.order_management.domain.exception.InvalidItemException;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    @Test
    void shouldCreateValidOrderItem() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");

        OrderItem item = OrderItem.of(productId, 2, unitPrice);

        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualTo(unitPrice);
    }

    @Test
    void shouldRejectNonPositiveQuantity() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");

        assertThatThrownBy(() -> OrderItem.of(productId, 0, unitPrice))
                .isInstanceOf(InvalidItemException.class);
    }

    @Test
    void shouldRejectNegativeUnitPrice() {
        UUID productId = UUID.randomUUID();
        Money negativePrice = Money.of(new BigDecimal("-1.00"), "USD");

        assertThatThrownBy(() -> OrderItem.of(productId, 1, negativePrice))
                .isInstanceOf(InvalidItemException.class);
    }

    @Test
    void shouldCalculateSubtotal() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");

        OrderItem item = OrderItem.of(productId, 3, unitPrice);

        Money subtotal = item.subtotal();

        assertThat(subtotal.getAmount()).isEqualByComparingTo("15.00");
        assertThat(subtotal.getCurrency()).isEqualTo("USD");
    }
}

