package com.apus.salehub.domain.service;

import com.apus.salehub.domain.model.stock.Stock;
import com.apus.salehub.domain.model.stock.StockMovement;
import com.apus.salehub.domain.valueobject.Quantity;

public class InventoryDomainService {

    public StockMovement adjustStock(Stock stock, Quantity newQuantity, String reason) {
        Quantity previousQuantity = stock.getQuantity();
        Quantity difference;

        if (newQuantity.isGreaterThan(previousQuantity)) {
            difference = Quantity.of(newQuantity.value() - previousQuantity.value());
            stock.increaseStock(difference);
        } else {
            difference = Quantity.of(previousQuantity.value() - newQuantity.value());
            stock.decreaseStock(difference);
        }

        return StockMovement.adjustment(stock.getProductId(), stock.getWarehouseId(), difference, reason);
    }

    public boolean shouldTriggerLowStockAlert(Stock stock) {
        return stock.isLowStock();
    }
}
