package com.apus.salehub.domain.model.stock;

public record StockMovementId(Long value) {
    public static StockMovementId of(Long value) {
        return new StockMovementId(value);
    }

    public static StockMovementId of(String value) {
        return new StockMovementId(Long.parseLong(value));
    }
}
