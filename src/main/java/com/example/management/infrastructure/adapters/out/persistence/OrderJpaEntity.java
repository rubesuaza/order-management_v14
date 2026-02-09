package com.example.management.infrastructure.adapters.out.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA para persistencia de pedidos.
 * Representa la estructura de datos en la base de datos.
 */
@Entity
@Table(name = "orders")
public class OrderJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String customerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatusJpa status;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineJpaEntity> lines = new ArrayList<>();
    
    public OrderJpaEntity() {
        // Requerido por JPA
    }
    
    public OrderJpaEntity(String customerId, OrderStatusJpa status) {
        this.customerId = customerId;
        this.status = status;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public OrderStatusJpa getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatusJpa status) {
        this.status = status;
    }
    
    public List<OrderLineJpaEntity> getLines() {
        return lines;
    }
    
    public void setLines(List<OrderLineJpaEntity> lines) {
        this.lines = lines;
    }
}

/**
 * Entidad JPA para líneas de pedido.
 */
@Entity
@Table(name = "order_lines")
class OrderLineJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;
    
    @Column(nullable = false)
    private String productId;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(nullable = false)
    private int quantity;
    
    public OrderLineJpaEntity() {
        // Requerido por JPA
    }
    
    public OrderLineJpaEntity(OrderJpaEntity order, String productId, BigDecimal unitPrice, int quantity) {
        this.order = order;
        this.productId = productId;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
    
    public String getId() {
        return id;
    }
    
    public OrderJpaEntity getOrder() {
        return order;
    }
    
    public void setOrder(OrderJpaEntity order) {
        this.order = order;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public int getQuantity() {
        return quantity;
    }
}

/**
 * Enumeración JPA para el estado del pedido.
 */
enum OrderStatusJpa {
    CREATED,
    CONFIRMED,
    CANCELED
}
