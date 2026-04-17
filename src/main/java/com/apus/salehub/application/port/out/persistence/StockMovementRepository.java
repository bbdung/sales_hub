package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.stock.StockMovement;
import com.apus.salehub.domain.model.stock.StockMovementId;
import com.apus.salehub.domain.model.warehouse.WarehouseId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface StockMovementRepository {
    Optional<StockMovement> findById(StockMovementId id);
    List<StockMovement> findByProduct(ProductId productId);
    List<StockMovement> findByWarehouse(WarehouseId warehouseId);
    List<StockMovement> findByProductAndWarehouse(ProductId productId, WarehouseId warehouseId);
    List<StockMovement> findByDateRange(Instant from, Instant to);
    StockMovement save(StockMovement movement);
}
