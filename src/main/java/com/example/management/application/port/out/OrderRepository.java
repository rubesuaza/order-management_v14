package com.example.management.application.port.out;

import com.example.management.domain.Order;

import java.util.Optional;

/**
 * Puerto de salida para persistencia de pedidos.
 * Define la interfaz que los adaptadores de persistencia deben implementar.
 */
public interface OrderRepository {
    
    /**
     * Guarda un pedido en el almacenamiento persistente.
     * 
     * @param order El pedido a guardar
     * @return El pedido guardado (puede incluir ID generado)
     */
    Order save(Order order);
    
    /**
     * Busca un pedido por su identificador.
     * 
     * @param orderId El identificador del pedido
     * @return El pedido si existe, Optional.empty() en caso contrario
     */
    Optional<Order> findById(String orderId);
    
    /**
     * Verifica si existe un pedido con el identificador dado.
     * 
     * @param orderId El identificador del pedido
     * @return true si existe, false en caso contrario
     */
    boolean existsById(String orderId);
}
