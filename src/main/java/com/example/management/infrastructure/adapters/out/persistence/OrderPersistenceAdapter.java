package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.Order;
import com.example.management.domain.OrderLine;
import com.example.management.domain.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de persistencia que implementa el puerto OrderRepository.
 * Traduce entre entidades de dominio y entidades JPA.
 */
@Component
public class OrderPersistenceAdapter implements OrderRepository {
    
    private final OrderJpaRepository jpaRepository;
    
    public OrderPersistenceAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Order save(Order order) {
        OrderJpaEntity jpaEntity = toJpaEntity(order);
        OrderJpaEntity saved = jpaRepository.save(jpaEntity);
        Order domainOrder = toDomainEntity(saved);
        // Establecer el ID generado en la entidad de dominio
        domainOrder.setId(saved.getId());
        return domainOrder;
    }
    
    @Override
    public Optional<Order> findById(String orderId) {
        return jpaRepository.findById(orderId)
                .map(this::toDomainEntity);
    }
    
    @Override
    public boolean existsById(String orderId) {
        return jpaRepository.existsById(orderId);
    }
    
    private OrderJpaEntity toJpaEntity(Order order) {
        OrderStatusJpa statusJpa = mapStatusToJpa(order.getStatus());
        OrderJpaEntity jpaEntity = new OrderJpaEntity(order.getCustomerId(), statusJpa);
        
        // Si el pedido tiene un ID (en caso de actualización), establecerlo
        if (order.getId() != null) {
            jpaEntity.setId(order.getId());
        }
        
        // Convertir líneas
        for (OrderLine line : order.getLines()) {
            OrderLineJpaEntity lineJpa = new OrderLineJpaEntity(
                    jpaEntity,
                    line.getProductId(),
                    line.getUnitPrice(),
                    line.getQuantity()
            );
            jpaEntity.getLines().add(lineJpa);
        }
        
        return jpaEntity;
    }
    
    private Order toDomainEntity(OrderJpaEntity jpaEntity) {
        Order order = Order.create(jpaEntity.getCustomerId());
        
        // Establecer ID si existe
        if (jpaEntity.getId() != null) {
            order.setId(jpaEntity.getId());
        }
        
        // Restaurar líneas
        for (OrderLineJpaEntity lineJpa : jpaEntity.getLines()) {
            order.addLine(
                    lineJpa.getProductId(),
                    lineJpa.getUnitPrice(),
                    lineJpa.getQuantity()
            );
        }
        
        // Restaurar estado
        OrderStatus status = mapStatusToDomain(jpaEntity.getStatus());
        if (status == OrderStatus.CONFIRMED) {
            order.confirm();
        } else if (status == OrderStatus.CANCELED) {
            order.cancel();
        }
        
        return order;
    }
    
    private OrderStatusJpa mapStatusToJpa(OrderStatus status) {
        return switch (status) {
            case CREATED -> OrderStatusJpa.CREATED;
            case CONFIRMED -> OrderStatusJpa.CONFIRMED;
            case CANCELED -> OrderStatusJpa.CANCELED;
        };
    }
    
    private OrderStatus mapStatusToDomain(OrderStatusJpa statusJpa) {
        return switch (statusJpa) {
            case CREATED -> OrderStatus.CREATED;
            case CONFIRMED -> OrderStatus.CONFIRMED;
            case CANCELED -> OrderStatus.CANCELED;
        };
    }
}
