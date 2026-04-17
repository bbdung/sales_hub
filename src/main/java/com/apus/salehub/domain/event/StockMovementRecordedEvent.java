package com.apus.salehub.domain.event;

import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.stock.StockMovementId;
import com.apus.salehub.domain.model.stock.StockMovementType;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import com.apus.salehub.domain.valueobject.Quantity;

import java.time.Instant;

public record StockMovementRecordedEvent(
    StockMovementId movementId,
    ProductId productId,
    WarehouseId warehouseId,
    StockMovementType type,
    Quantity quantity,
    Instant occurredAt
) {
    public static StockMovementRecordedEvent of(StockMovementId movementId, ProductId productId,
                                                 WarehouseId warehouseId, StockMovementType type, Quantity quantity) {
        return new StockMovementRecordedEvent(movementId, productId, warehouseId, type, quantity, Instant.now());
    }
}
