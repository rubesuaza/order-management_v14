package com.example.management.infrastructure.adapters.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de contrato para el adaptador REST.
 * Verifica que los endpoints están correctamente configurados.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crearPedidoDebeRetornarNotImplemented() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content("{\"customerId\":\"CUST-001\"}"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void obtenerPedidoDebeRetornarNotImplemented() throws Exception {
        mockMvc.perform(get("/api/orders/ORDER-001"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void agregarLineaDebeRetornarNotImplemented() throws Exception {
        mockMvc.perform(post("/api/orders/ORDER-001/lines")
                        .contentType("application/json")
                        .content("{\"productId\":\"PROD-001\",\"unitPrice\":10.50,\"quantity\":2}"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void confirmarPedidoDebeRetornarNotImplemented() throws Exception {
        mockMvc.perform(post("/api/orders/ORDER-001/confirm"))
                .andExpect(status().isNotImplemented());
    }

    @Test
    void cancelarPedidoDebeRetornarNotImplemented() throws Exception {
        mockMvc.perform(post("/api/orders/ORDER-001/cancel"))
                .andExpect(status().isNotImplemented());
    }
}
