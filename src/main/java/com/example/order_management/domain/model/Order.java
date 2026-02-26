package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidOrderStateException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private static final BigDecimal MIN_ORDER_VALUE = new BigDecimal("10.00");

    private final UUID id;
    private final UUID customerId;
    private final LocalDateTime createdAt;
    private final List<OrderItem> items;
    private Money totalAmount;
    private OrderStatus status;

    private Order(UUID id, UUID customerId, LocalDateTime createdAt, List<OrderItem> items, OrderStatus status) {
        if (customerId == null) {
            throw new InvalidOrderStateException("CustomerId cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderStateException("Order must contain at least one item");
        }
        this.id = id;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.items = List.copyOf(items);
        this.status = status;
        this.totalAmount = calculateTotal();
    }

    public static Order create(UUID customerId, List<OrderItem> items) {
        return new Order(UUID.randomUUID(), customerId, LocalDateTime.now(), items, OrderStatus.PENDING);
    }

    private Money calculateTotal() {
        Money total = null;
        for (OrderItem item : items) {
            if (total == null) {
                total = item.subtotal();
            } else {
                total = total.add(item.subtotal());
            }
        }
        return Objects.requireNonNullElseGet(total, () -> Money.of(BigDecimal.ZERO));
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only pending orders can be paid");
        }
        if (totalAmount.getAmount().compareTo(MIN_ORDER_VALUE) < 0) {
            throw new InvalidOrderStateException("Order total must be at least " + MIN_ORDER_VALUE);
        }
        this.status = OrderStatus.PAID;
    }

    public void markAsShipped() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Only paid orders can be shipped");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void cancel() {
        if (status != OrderStatus.PENDING && status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Only pending or paid orders can be cancelled");
        }
        this.status = OrderStatus.CANCELLED;
    }
}

