package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.stock.Stock;
import com.apus.salehub.domain.model.stock.StockId;
import com.apus.salehub.domain.model.warehouse.WarehouseId;

import java.util.List;
import java.util.Optional;

public interface StockRepository {
    Optional<Stock> findById(StockId id);
    Optional<Stock> findByProductAndWarehouse(ProductId productId, WarehouseId warehouseId);
    List<Stock> findByProduct(ProductId productId);
    List<Stock> findByWarehouse(WarehouseId warehouseId);
    List<Stock> findLowStock();
    Stock save(Stock stock);
    void delete(StockId id);
}
