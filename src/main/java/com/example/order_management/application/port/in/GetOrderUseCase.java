package com.example.order_management.application.port.in;

import com.example.order_management.domain.model.Order;

import java.util.Optional;
import java.util.UUID;

public interface GetOrderUseCase {

    Optional<Order> getOrderById(UUID orderId);
}

