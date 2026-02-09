package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.port.out.OrderRepository;
import com.example.management.domain.Order;
import com.example.management.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test de integración para el adaptador de persistencia.
 * Verifica el contrato del puerto OrderRepository usando una base de datos en memoria.
 */
@DataJpaTest
@Import(OrderPersistenceAdapter.class)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class OrderPersistenceAdapterTest {

    @Autowired
    private OrderJpaRepository jpaRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
    }

    @Test
    void debeGuardarUnPedidoYRecuperarlo() {
        // Given
        Order order = Order.create("CUST-001");
        order.addLine("PROD-001", new BigDecimal("10.50"), 2);
        order.addLine("PROD-002", new BigDecimal("5.25"), 3);

        // When
        Order saved = orderRepository.save(order);
        Optional<Order> found = orderRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo("CUST-001");
        assertThat(found.get().getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(found.get().getLines()).hasSize(2);
        assertThat(found.get().getTotal()).isEqualByComparingTo(new BigDecimal("36.75"));
    }

    @Test
    void debeGuardarUnPedidoConfirmadoYRestaurarSuEstado() {
        // Given
        Order order = Order.create("CUST-002");
        order.addLine("PROD-003", new BigDecimal("15.00"), 1);
        order.confirm();

        // When
        Order saved = orderRepository.save(order);
        Optional<Order> found = orderRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void debeGuardarUnPedidoCanceladoYRestaurarSuEstado() {
        // Given
        Order order = Order.create("CUST-003");
        order.addLine("PROD-004", new BigDecimal("20.00"), 1);
        order.cancel();

        // When
        Order saved = orderRepository.save(order);
        Optional<Order> found = orderRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    void debeActualizarUnPedidoExistente() {
        // Given
        Order order = Order.create("CUST-004");
        order.addLine("PROD-005", new BigDecimal("10.00"), 1);
        Order saved = orderRepository.save(order);

        // When - agregar una línea más
        Order toUpdate = orderRepository.findById(saved.getId()).orElseThrow();
        toUpdate.addLine("PROD-006", new BigDecimal("5.00"), 2);
        Order updated = orderRepository.save(toUpdate);

        // Then
        Optional<Order> found = orderRepository.findById(updated.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getLines()).hasSize(2);
        assertThat(found.get().getTotal()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void debeRetornarEmptyCuandoNoExisteElPedido() {
        // When
        Optional<Order> found = orderRepository.findById("NON-EXISTENT");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void debeVerificarSiExisteUnPedido() {
        // Given
        Order order = Order.create("CUST-005");
        order.addLine("PROD-007", new BigDecimal("10.00"), 1);
        Order saved = orderRepository.save(order);

        // When & Then
        assertThat(orderRepository.existsById(saved.getId())).isTrue();
        assertThat(orderRepository.existsById("NON-EXISTENT")).isFalse();
    }

    @Test
    void debeGenerarIdAlGuardarUnPedidoNuevo() {
        // Given
        Order order = Order.create("CUST-006");
        order.addLine("PROD-008", new BigDecimal("10.00"), 1);

        // When
        Order saved = orderRepository.save(order);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getId()).isNotBlank();
    }
}
