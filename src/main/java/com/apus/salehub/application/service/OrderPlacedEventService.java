package com.apus.salehub.application.service;

import com.apus.salehub.adapter.in.messaging.dto.OrderPlacedMessage;
import com.apus.salehub.application.port.in.messaging.OrderPlacedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderPlacedEventService implements OrderPlacedEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderPlacedEventService.class);

    @Override
    public void handle(OrderPlacedMessage event) {
        log.info("[ORDER-PLACED] Processing order={}", event.orderId());
        // TODO: Implement stock reservation logic for incoming orders
    }
}
