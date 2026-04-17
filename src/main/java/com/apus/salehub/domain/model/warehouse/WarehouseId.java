package com.apus.salehub.domain.model.warehouse;

public record WarehouseId(Long value) {
    public static WarehouseId of(Long value) {
        return new WarehouseId(value);
    }

    public static WarehouseId of(String value) {
        return new WarehouseId(Long.parseLong(value));
    }
}
