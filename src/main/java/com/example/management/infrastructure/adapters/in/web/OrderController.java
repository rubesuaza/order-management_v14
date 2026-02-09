package com.example.management.infrastructure.adapters.in.web;

import com.example.management.domain.Order;
import com.example.management.domain.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controlador REST para gestión de pedidos.
 * Adaptador de entrada que expone la API HTTP.
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    // TODO: Inyectar caso de uso cuando se implemente la capa de aplicación
    // Por ahora, este es un placeholder que muestra la estructura del adaptador
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        // TODO: Implementar con caso de uso
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        // TODO: Implementar con caso de uso
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    @PostMapping("/{orderId}/lines")
    public ResponseEntity<Void> addLine(
            @PathVariable String orderId,
            @RequestBody AddLineRequest request) {
        // TODO: Implementar con caso de uso
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Void> confirmOrder(@PathVariable String orderId) {
        // TODO: Implementar con caso de uso
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        // TODO: Implementar con caso de uso
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    // DTOs para requests y responses
    record CreateOrderRequest(String customerId) {}
    
    record AddLineRequest(String productId, BigDecimal unitPrice, Integer quantity) {}
    
    record OrderResponse(
            String id,
            String customerId,
            OrderStatus status,
            BigDecimal total,
            java.util.List<OrderLineResponse> lines
    ) {}
    
    record OrderLineResponse(
            String productId,
            BigDecimal unitPrice,
            Integer quantity,
            BigDecimal lineTotal
    ) {}
}
