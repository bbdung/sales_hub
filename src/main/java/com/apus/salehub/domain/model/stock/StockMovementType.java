package com.apus.salehub.domain.model.stock;

public enum StockMovementType {
    IN,          // Stock received
    OUT,         // Stock shipped/sold
    TRANSFER,    // Transfer between warehouses
    ADJUSTMENT   // Manual adjustment (inventory count)
}
