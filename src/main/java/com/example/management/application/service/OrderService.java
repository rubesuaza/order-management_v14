package com.example.management.application.service;

import com.example.management.application.port.in.OrderManagementPort;
import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Servicio de aplicación para gestión de pedidos.
 * Implementa la lógica de orquestación de casos de uso.
 */
@Service
@Transactional
public class OrderService implements OrderManagementPort {
    
    private final OrderRepository orderRepository;
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public Order createOrder(String customerId) {
        Order order = Order.create(customerId);
        return orderRepository.save(order);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }
    
    @Override
    public Order addLineToOrder(String orderId, String productId, BigDecimal unitPrice, int quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        
        order.addLine(productId, unitPrice, quantity);
        return orderRepository.save(order);
    }
    
    @Override
    public Order confirmOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        
        order.confirm();
        return orderRepository.save(order);
    }
    
    @Override
    public Order cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + orderId));
        
        order.cancel();
        return orderRepository.save(order);
    }
}
