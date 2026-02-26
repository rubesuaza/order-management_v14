package com.example.order_management.application.port.out;

import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findByCustomerId(UUID customerId);

    List<Order> findByStatus(OrderStatus status);
}

