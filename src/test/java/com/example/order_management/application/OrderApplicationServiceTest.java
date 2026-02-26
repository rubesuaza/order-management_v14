package com.example.order_management.application;

import com.example.order_management.application.port.in.CreateOrderUseCase.CreateOrderCommand;
import com.example.order_management.application.port.in.CreateOrderUseCase.CreateOrderItemCommand;
import com.example.order_management.application.port.out.OrderRepositoryPort;
import com.example.order_management.application.service.OrderApplicationService;
import com.example.order_management.domain.exception.InvalidOrderStateException;
import com.example.order_management.domain.model.Money;
import com.example.order_management.domain.model.Order;
import com.example.order_management.domain.model.OrderItem;
import com.example.order_management.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderApplicationServiceTest {

    private final OrderRepositoryPort orderRepository = mock(OrderRepositoryPort.class);
    private final OrderApplicationService service = new OrderApplicationService(orderRepository);

    @Test
    void createOrder_shouldSaveOrderAndReturnGeneratedId() {
        UUID customerId = UUID.randomUUID();
        CreateOrderItemCommand itemCommand = new CreateOrderItemCommand(
                UUID.randomUUID(),
                2,
                new BigDecimal("5.00")
        );
        CreateOrderCommand command = new CreateOrderCommand(customerId, List.of(itemCommand));

        Order orderToSave = Order.create(
                customerId,
                List.of(OrderItem.of(itemCommand.productId(), itemCommand.quantity(), Money.of(itemCommand.unitPrice())))
        );
        Order savedOrder = Order.create(
                customerId,
                List.of(OrderItem.of(itemCommand.productId(), itemCommand.quantity(), Money.of(itemCommand.unitPrice())))
        );

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        UUID resultId = service.createOrder(command);

        assertThat(resultId).isEqualTo(savedOrder.getId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_shouldRejectNullCommandOrEmptyItems() {
        assertThatThrownBy(() -> service.createOrder(null))
                .isInstanceOf(InvalidOrderStateException.class);

        CreateOrderCommand commandWithNullItems = new CreateOrderCommand(UUID.randomUUID(), null);
        assertThatThrownBy(() -> service.createOrder(commandWithNullItems))
                .isInstanceOf(InvalidOrderStateException.class);

        CreateOrderCommand commandWithEmptyItems = new CreateOrderCommand(UUID.randomUUID(), List.of());
        assertThatThrownBy(() -> service.createOrder(commandWithEmptyItems))
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    void getOrderById_shouldDelegateToRepository() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.create(
                UUID.randomUUID(),
                List.of(OrderItem.of(UUID.randomUUID(), 2, Money.of(new BigDecimal("5.00"))))
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Optional<Order> result = service.getOrderById(orderId);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(order);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void payOrder_shouldMarkOrderAsPaidAndSave() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.create(
                UUID.randomUUID(),
                List.of(OrderItem.of(UUID.randomUUID(), 2, Money.of(new BigDecimal("5.00"))))
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        service.payOrder(orderId);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
    }

    @Test
    void payOrder_shouldThrowWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.payOrder(orderId))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Order not found");

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void payOrder_shouldNotSaveWhenBusinessRuleFails() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.create(
                UUID.randomUUID(),
                List.of(OrderItem.of(UUID.randomUUID(), 1, Money.of(new BigDecimal("5.00"))))
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.payOrder(orderId))
                .isInstanceOf(InvalidOrderStateException.class);

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any(Order.class));
    }
}

