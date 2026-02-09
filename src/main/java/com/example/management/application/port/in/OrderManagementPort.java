package com.example.management.application.port.in;

import com.example.management.domain.Order;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Puerto de entrada para gestión de pedidos.
 * Define las operaciones que los adaptadores de entrada pueden invocar.
 */
public interface OrderManagementPort {
    
    /**
     * Crea un nuevo pedido.
     * 
     * @param customerId Identificador del cliente
     * @return El pedido creado
     */
    Order createOrder(String customerId);
    
    /**
     * Obtiene un pedido por su identificador.
     * 
     * @param orderId Identificador del pedido
     * @return El pedido si existe, Optional.empty() en caso contrario
     */
    Optional<Order> getOrder(String orderId);
    
    /**
     * Añade una línea a un pedido existente.
     * 
     * @param orderId Identificador del pedido
     * @param productId Identificador del producto
     * @param unitPrice Precio unitario
     * @param quantity Cantidad
     * @return El pedido actualizado
     * @throws IllegalArgumentException si el pedido no existe o no se puede modificar
     */
    Order addLineToOrder(String orderId, String productId, BigDecimal unitPrice, int quantity);
    
    /**
     * Confirma un pedido.
     * 
     * @param orderId Identificador del pedido
     * @return El pedido confirmado
     * @throws IllegalArgumentException si el pedido no existe o no se puede confirmar
     */
    Order confirmOrder(String orderId);
    
    /**
     * Cancela un pedido.
     * 
     * @param orderId Identificador del pedido
     * @return El pedido cancelado
     * @throws IllegalArgumentException si el pedido no existe o no se puede cancelar
     */
    Order cancelOrder(String orderId);
}
