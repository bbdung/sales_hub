package com.apus.salehub.adapter.in.rest.dto;

import com.apus.salehub.application.port.in.stock.CheckStockUseCase.CheckStockResult;

public record CheckStockResponse(
    Long stockId,
    Long productId,
    Long warehouseId,
    int totalQuantity,
    int reservedQuantity,
    int availableQuantity,
    boolean lowStock
) {
    public static CheckStockResponse from(CheckStockResult result) {
        return new CheckStockResponse(
                result.stockId(),
                result.productId(),
                result.warehouseId(),
                result.totalQuantity(),
                result.reservedQuantity(),
                result.availableQuantity(),
                result.lowStock()
        );
    }
}
