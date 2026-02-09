package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.Order;
import com.example.management.domain.OrderLine;
import com.example.management.domain.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;
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
        // Reconstruir la entidad de dominio con el ID generado por la persistencia
        return toDomainEntity(saved);
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
        // Usar el constructor apropiado según si el pedido tiene ID (actualización) o no (nuevo)
        OrderJpaEntity jpaEntity = order.getId() != null
                ? new OrderJpaEntity(order.getId(), order.getCustomerId(), statusJpa)
                : new OrderJpaEntity(order.getCustomerId(), statusJpa);
        
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
        // Convertir líneas JPA a líneas de dominio
        List<OrderLine> domainLines = jpaEntity.getLines().stream()
                .map(lineJpa -> new OrderLine(
                        lineJpa.getProductId(),
                        lineJpa.getUnitPrice(),
                        lineJpa.getQuantity()))
                .toList();
        
        // Mapear estado
        OrderStatus status = mapStatusToDomain(jpaEntity.getStatus());
        
        // Reconstruir la entidad de dominio usando el método de fábrica reconstruct
        // que establece el ID de forma inmutable durante la construcción
        return Order.reconstruct(
                jpaEntity.getId(),
                jpaEntity.getCustomerId(),
                status,
                domainLines
        );
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
