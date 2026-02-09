package com.example.management.infrastructure.adapters.in.web;

import com.example.management.application.port.in.OrderManagementPort;
import com.example.management.domain.Order;
import com.example.management.domain.OrderLine;
import com.example.management.domain.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de pedidos.
 * Adaptador de entrada que expone la API HTTP.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderManagementPort orderManagementPort;
    
    public OrderController(OrderManagementPort orderManagementPort) {
        this.orderManagementPort = orderManagementPort;
    }
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderManagementPort.createOrder(request.customerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toOrderResponse(order));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        return orderManagementPort.getOrder(orderId)
                .map(order -> ResponseEntity.ok(toOrderResponse(order)))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{orderId}/lines")
    public ResponseEntity<OrderResponse> addLine(
            @PathVariable String orderId,
            @RequestBody AddLineRequest request) {
        try {
            Order order = orderManagementPort.addLineToOrder(
                    orderId,
                    request.productId(),
                    request.unitPrice(),
                    request.quantity()
            );
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable String orderId) {
        try {
            Order order = orderManagementPort.confirmOrder(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable String orderId) {
        try {
            Order order = orderManagementPort.cancelOrder(orderId);
            return ResponseEntity.ok(toOrderResponse(order));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    private OrderResponse toOrderResponse(Order order) {
        List<OrderLineResponse> lineResponses = order.getLines().stream()
                .map(line -> new OrderLineResponse(
                        line.getProductId(),
                        line.getUnitPrice(),
                        line.getQuantity(),
                        line.getLineTotal()
                ))
                .toList();
        
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                mapStatusToPresentation(order.getStatus()),
                order.getTotal(),
                lineResponses
        );
    }
    
    private OrderStatusPresentation mapStatusToPresentation(OrderStatus domainStatus) {
        return switch (domainStatus) {
            case CREATED -> OrderStatusPresentation.CREATED;
            case CONFIRMED -> OrderStatusPresentation.CONFIRMED;
            case CANCELED -> OrderStatusPresentation.CANCELED;
        };
    }
    
    // DTOs para requests y responses
    record CreateOrderRequest(String customerId) {}
    
    record AddLineRequest(String productId, BigDecimal unitPrice, Integer quantity) {}
    
    record OrderResponse(
            String id,
            String customerId,
            OrderStatusPresentation status,
            BigDecimal total,
            List<OrderLineResponse> lines
    ) {}
    
    record OrderLineResponse(
            String productId,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal lineTotal
    ) {}
    
    /**
     * Enum de presentación para el estado del pedido.
     * Evita acoplar la capa de presentación al dominio.
     */
    enum OrderStatusPresentation {
        CREATED,
        CONFIRMED,
        CANCELED
    }
}
