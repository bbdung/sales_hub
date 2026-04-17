package com.apus.salehub.domain.event;

import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import com.apus.salehub.domain.valueobject.Quantity;

import java.time.Instant;

public record StockUpdatedEvent(
    ProductId productId,
    WarehouseId warehouseId,
    Quantity previousQuantity,
    Quantity newQuantity,
    String reason,
    Instant occurredAt
) {
    public static StockUpdatedEvent of(ProductId productId, WarehouseId warehouseId,
                                       Quantity previousQuantity, Quantity newQuantity, String reason) {
        return new StockUpdatedEvent(productId, warehouseId, previousQuantity, newQuantity, reason, Instant.now());
    }
}
