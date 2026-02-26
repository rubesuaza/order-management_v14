package com.example.order_management.domain.model;

import com.example.order_management.domain.exception.InvalidItemException;

import java.util.UUID;

public class OrderItem {

    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;

    private OrderItem(UUID productId, int quantity, Money unitPrice) {
        if (productId == null) {
            throw new InvalidItemException("ProductId cannot be null");
        }
        if (quantity <= 0) {
            throw new InvalidItemException("Quantity must be greater than zero");
        }
        if (unitPrice == null) {
            throw new InvalidItemException("Unit price cannot be null");
        }
        if (unitPrice.getAmount().signum() < 0) {
            throw new InvalidItemException("Unit price cannot be negative");
        }
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static OrderItem of(UUID productId, int quantity, Money unitPrice) {
        return new OrderItem(productId, quantity, unitPrice);
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }
}

