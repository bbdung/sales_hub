package com.apus.salehub.adapter.in.rest.dto;

import com.apus.salehub.application.port.in.stock.AdjustStockUseCase.AdjustStockResult;

public record StockResponse(
    Long stockId,
    int previousQuantity,
    int newQuantity,
    boolean lowStockAlert
) {
    public static StockResponse from(AdjustStockResult result) {
        return new StockResponse(
                result.stockId(),
                result.previousQuantity(),
                result.newQuantity(),
                result.lowStockAlert()
        );
    }
}
