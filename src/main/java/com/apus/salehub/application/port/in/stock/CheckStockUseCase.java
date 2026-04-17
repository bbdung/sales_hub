package com.apus.salehub.application.port.in.stock;

public interface CheckStockUseCase {

    record CheckStockQuery(
        Long productId,
        Long warehouseId
    ) {}

    record CheckStockResult(
        Long stockId,
        Long productId,
        Long warehouseId,
        int totalQuantity,
        int reservedQuantity,
        int availableQuantity,
        boolean lowStock
    ) {}

    CheckStockResult execute(CheckStockQuery query);
}
