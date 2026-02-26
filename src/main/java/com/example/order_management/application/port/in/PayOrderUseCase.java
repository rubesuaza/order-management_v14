package com.example.order_management.application.port.in;

import java.util.UUID;

public interface PayOrderUseCase {

    void payOrder(UUID orderId);
}

