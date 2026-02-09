package com.example.management.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Línea de pedido como entidad hija de {@link Order}.
 * Contiene sólo lógica de dominio y usa únicamente la librería estándar.
 */
final class OrderLine {

    private final String productId;
    private final BigDecimal unitPrice;
    private final int quantity;

    OrderLine(String productId, BigDecimal unitPrice, int quantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("El identificador de producto es obligatorio");
        }
        if (unitPrice == null) {
            throw new IllegalArgumentException("El precio unitario es obligatorio");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        this.productId = productId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    String getProductId() {
        return productId;
    }

    BigDecimal getUnitPrice() {
        return unitPrice;
    }

    int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderLine)) return false;
        OrderLine orderLine = (OrderLine) o;
        return quantity == orderLine.quantity
                && Objects.equals(productId, orderLine.productId)
                && Objects.equals(unitPrice, orderLine.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, unitPrice, quantity);
    }
}

