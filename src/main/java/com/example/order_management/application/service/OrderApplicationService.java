package com.example.order_management.application.service;

import com.example.order_management.application.port.in.CreateOrderUseCase;
import com.example.order_management.application.port.in.GetOrderUseCase;
import com.example.order_management.application.port.in.PayOrderUseCase;
import com.example.order_management.application.port.out.OrderRepositoryPort;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderApplicationService implements
        CreateOrderUseCase,
        GetOrderUseCase,
        PayOrderUseCase {

    private final OrderRepositoryPort orderRepository;

    public OrderApplicationService(OrderRepositoryPort orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public UUID createOrder(CreateOrderCommand command) {
        if (command == null || command.items() == null || command.items().isEmpty()) {
            throw new InvalidOrderStateException("Order must contain at least one item");
        }

        List<OrderItem> items = command.items().stream()
                .map(item -> OrderItem.of(
                        item.productId(),
                        item.quantity(),
                        Money.of(item.unitPrice())
                ))
                .toList();

        Order order = Order.create(command.customerId(), items);
        Order saved = orderRepository.save(order);
        return saved.getId();
    }

    @Override
    public Optional<Order> getOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public void payOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidOrderStateException("Order not found: " + orderId));

        order.markAsPaid();
        orderRepository.save(order);
    }
}

