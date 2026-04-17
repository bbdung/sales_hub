package com.apus.salehub.application.port.in.stock;

public interface AdjustStockUseCase {

    record AdjustStockCommand(
        Long productId,
        Long warehouseId,
        int newQuantity,
        String reason
    ) {}

    record AdjustStockResult(
        Long stockId,
        int previousQuantity,
        int newQuantity,
        boolean lowStockAlert
    ) {}

    AdjustStockResult execute(AdjustStockCommand command);
}
