package com.apus.salehub.application.port.in.messaging;

import com.apus.salehub.adapter.in.messaging.dto.OrderPlacedMessage;

/**
 * Inbound port for handling order-placed events from pos_service.
 * Implementations will contain the business logic for stock reservation
 * when an order is placed.
 */
public interface OrderPlacedEventHandler {

    void handle(OrderPlacedMessage event);
}
