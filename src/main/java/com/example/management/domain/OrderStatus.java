package com.example.management.domain;

/**
 * Estado del pedido dentro del dominio.
 *
 * Esta enumeración es deliberadamente simple y no depende de ningún framework.
 */
public enum OrderStatus {
    CREATED,
    CONFIRMED,
    CANCELED
}

