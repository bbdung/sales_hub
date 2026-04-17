package com.apus.salehub.application.port.out.messaging;

import com.apus.salehub.domain.event.LowStockAlertEvent;
import com.apus.salehub.domain.event.StockMovementRecordedEvent;
import com.apus.salehub.domain.event.StockUpdatedEvent;

public interface EventPublisher {
    void publish(StockUpdatedEvent event);
    void publish(LowStockAlertEvent event);
    void publish(StockMovementRecordedEvent event);
}
