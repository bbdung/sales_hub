package com.apus.salehub.domain.model.product;

public record ProductId(Long value) {
    public static ProductId of(Long value) {
        return new ProductId(value);
    }

    public static ProductId of(String value) {
        return new ProductId(Long.parseLong(value));
    }
}
