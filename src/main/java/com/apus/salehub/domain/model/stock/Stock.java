package com.apus.salehub.domain.model.stock;

import com.apus.base.exception.CommandExceptionBuilder;
import com.apus.base.response.ErrorCode;
import com.apus.common.drive.ErrorCodes;
import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import com.apus.salehub.domain.valueobject.Quantity;

public class Stock {
    private StockId id;
    private ProductId productId;
    private WarehouseId warehouseId;
    private Quantity quantity;
    private Quantity reservedQuantity;
    private Quantity minimumLevel;

    private Stock() {
    }

    public Stock(StockId id, ProductId productId, WarehouseId warehouseId,
                 Quantity quantity, Quantity minimumLevel) {
        this.id = id;
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.reservedQuantity = Quantity.zero();
        this.minimumLevel = minimumLevel;
    }

    public static Stock reconstitute(StockId id, ProductId productId, WarehouseId warehouseId,
                                     Quantity quantity, Quantity reservedQuantity, Quantity minimumLevel) {
        var stock = new Stock();
        stock.id = id;
        stock.productId = productId;
        stock.warehouseId = warehouseId;
        stock.quantity = quantity;
        stock.reservedQuantity = reservedQuantity;
        stock.minimumLevel = minimumLevel;
        return stock;
    }

    public StockId getId() {
        return id;
    }

    public ProductId getProductId() {
        return productId;
    }

    public WarehouseId getWarehouseId() {
        return warehouseId;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Quantity getReservedQuantity() {
        return reservedQuantity;
    }

    public Quantity getMinimumLevel() {
        return minimumLevel;
    }

    public Quantity getAvailableQuantity() {
        return quantity.subtract(reservedQuantity);
    }

    public boolean isLowStock() {
        return quantity.isLessThanOrEqual(minimumLevel);
    }

    public void increaseStock(Quantity amount) {
        this.quantity = this.quantity.add(amount);
    }

    public void decreaseStock(Quantity amount) {
        if (getAvailableQuantity().isLessThan(amount)) {
            // "stock.insufficient_available"
            throw CommandExceptionBuilder.exception(ErrorCodes.ERROR_0002);
        }
        this.quantity = this.quantity.subtract(amount);
    }

    public void reserve(Quantity amount) {
        if (getAvailableQuantity().isLessThan(amount)) {
            throw CommandExceptionBuilder.exception(ErrorCodes.ERROR_0002);
            //  throw new DomainException("stock.insufficient_reserve");
        }
        this.reservedQuantity = this.reservedQuantity.add(amount);
    }

    public void releaseReservation(Quantity amount) {
        this.reservedQuantity = this.reservedQuantity.subtract(amount);
    }
}
