package com.apus.salehub.domain.model.stock;

import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import com.apus.salehub.domain.valueobject.Quantity;

import java.time.Instant;

public class StockMovement {
    private StockMovementId id;
    private ProductId productId;
    private WarehouseId warehouseId;
    private StockMovementType type;
    private Quantity quantity;
    private String reference;
    private String reason;
    private Instant createdAt;

    protected StockMovement() {}

    public StockMovement(StockMovementId id, ProductId productId, WarehouseId warehouseId,
                         StockMovementType type, Quantity quantity, String reference, String reason) {
        this.id = id;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.type = type;
        this.quantity = quantity;
        this.reference = reference;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public static StockMovement stockIn(ProductId productId, WarehouseId warehouseId,
                                         Quantity quantity, String reference, String reason) {
        return new StockMovement(null, productId, warehouseId,
                StockMovementType.IN, quantity, reference, reason);
    }

    public static StockMovement stockOut(ProductId productId, WarehouseId warehouseId,
                                          Quantity quantity, String reference, String reason) {
        return new StockMovement(null, productId, warehouseId,
                StockMovementType.OUT, quantity, reference, reason);
    }

    public static StockMovement adjustment(ProductId productId, WarehouseId warehouseId,
                                            Quantity quantity, String reason) {
        return new StockMovement(null, productId, warehouseId,
                StockMovementType.ADJUSTMENT, quantity, null, reason);
    }

    public StockMovementId getId() { return id; }
    public ProductId getProductId() { return productId; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public StockMovementType getType() { return type; }
    public Quantity getQuantity() { return quantity; }
    public String getReference() { return reference; }
    public String getReason() { return reason; }
    public Instant getCreatedAt() { return createdAt; }
}
