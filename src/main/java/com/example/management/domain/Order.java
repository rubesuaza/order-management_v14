package com.example.management.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad raíz de pedido dentro del dominio de gestión de pedidos.
 *
 * Esta clase no depende de ningún framework ni capa externa;
 * sólo contiene lógica de negocio y utiliza la librería estándar.
 */
public final class Order {

    private String id;
    private final String customerId;
    private final List<OrderLine> lines = new ArrayList<>();
    private OrderStatus status = OrderStatus.CREATED;

    private Order(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        this.customerId = customerId;
    }

    /**
     * Fábrica estática para garantizar que ningún pedido se cree en
     * un estado inválido.
     */
    public static Order create(String customerId) {
        return new Order(customerId);
    }

    /**
     * Añade una línea al pedido respetando las invariantes:
     * - No se permite modificar pedidos confirmados o cancelados.
     * - La cantidad debe ser mayor que cero.
     * - El precio unitario no puede ser negativo.
     */
    public void addLine(String productId, BigDecimal unitPrice, int quantity) {
        ensureModifiable();

        // La validación de cantidad y precio se delega a OrderLine,
        // pero mantenemos aquí también la intención de negocio.
        lines.add(new OrderLine(productId, unitPrice, quantity));
    }

    /**
     * Calcula el total del pedido como suma de los totales de las líneas.
     */
    public BigDecimal getTotal() {
        return lines.stream()
                .map(OrderLine::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Devuelve una copia inmutable de las líneas para no exponer
     * el estado interno.
     */
    public List<OrderLine> getLines() {
        return Collections.unmodifiableList(new ArrayList<>(lines));
    }

    /**
     * Regla de negocio:
     * - No se puede confirmar un pedido sin líneas.
     * - Sólo se puede confirmar un pedido en estado CREATED.
     */
    public void confirm() {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Sólo se pueden confirmar pedidos en estado CREATED");
        }
        if (lines.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar un pedido sin líneas");
        }
        status = OrderStatus.CONFIRMED;
    }

    /**
     * Regla de negocio:
     * - No se puede cancelar un pedido ya cancelado.
     * - Se permite cancelar tanto en estado CREATED como CONFIRMED.
     */
    public void cancel() {
        if (status == OrderStatus.CANCELED) {
            throw new IllegalStateException("El pedido ya está cancelado");
        }
        status = OrderStatus.CANCELED;
    }

    private void ensureModifiable() {
        if (status == OrderStatus.CONFIRMED || status == OrderStatus.CANCELED) {
            throw new IllegalStateException("No se pueden modificar pedidos confirmados o cancelados");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        // Si ambos tienen ID, comparar por ID; si no, comparar por contenido
        if (id != null && order.id != null) {
            return Objects.equals(id, order.id);
        }
        return Objects.equals(customerId, order.customerId)
                && status == order.status
                && Objects.equals(lines, order.lines);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(customerId, status, lines);
    }
}

