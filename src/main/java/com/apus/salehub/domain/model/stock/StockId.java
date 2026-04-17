package com.apus.salehub.domain.model.stock;

public record StockId(Long value) {
    public static StockId of(Long value) {
        return new StockId(value);
    }

    public static StockId of(String value) {
        return new StockId(Long.parseLong(value));
    }
}
