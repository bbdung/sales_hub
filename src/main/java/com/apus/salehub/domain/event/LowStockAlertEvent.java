package com.apus.salehub.domain.event;

import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import com.apus.salehub.domain.valueobject.Quantity;

import java.time.Instant;

public record LowStockAlertEvent(
    ProductId productId,
    WarehouseId warehouseId,
    Quantity currentQuantity,
    Quantity minimumLevel,
    Instant occurredAt
) {
    public static LowStockAlertEvent of(ProductId productId, WarehouseId warehouseId,
                                        Quantity currentQuantity, Quantity minimumLevel) {
        return new LowStockAlertEvent(productId, warehouseId, currentQuantity, minimumLevel, Instant.now());
    }
}
