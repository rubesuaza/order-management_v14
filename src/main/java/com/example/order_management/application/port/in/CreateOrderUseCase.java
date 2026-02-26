package com.example.order_management.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CreateOrderUseCase {

    UUID createOrder(CreateOrderCommand command);

    record CreateOrderItemCommand(
            UUID productId,
            int quantity,
            BigDecimal unitPrice
    ) {
    }

    record CreateOrderCommand(
            UUID customerId,
            List<CreateOrderItemCommand> items
    ) {
    }
}

