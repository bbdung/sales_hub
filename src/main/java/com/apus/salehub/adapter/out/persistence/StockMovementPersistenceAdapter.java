package com.apus.salehub.adapter.out.persistence;

import com.apus.salehub.application.port.out.persistence.StockMovementRepository;
import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.stock.StockMovement;
import com.apus.salehub.domain.model.stock.StockMovementId;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class StockMovementPersistenceAdapter implements StockMovementRepository {

    private final JdbcClient jdbcClient;

    public StockMovementPersistenceAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<StockMovement> findById(StockMovementId id) {
        // TODO: Implement query
        return Optional.empty();
    }

    @Override
    public List<StockMovement> findByProduct(ProductId productId) {
        // TODO: Implement query
        return List.of();
    }

    @Override
    public List<StockMovement> findByWarehouse(WarehouseId warehouseId) {
        // TODO: Implement query
        return List.of();
    }

    @Override
    public List<StockMovement> findByProductAndWarehouse(ProductId productId, WarehouseId warehouseId) {
        // TODO: Implement query
        return List.of();
    }

    @Override
    public List<StockMovement> findByDateRange(Instant from, Instant to) {
        // TODO: Implement query
        return List.of();
    }

    @Override
    public StockMovement save(StockMovement movement) {
        jdbcClient.sql("""
                INSERT INTO stock_movement (product_id, warehouse_id, type, quantity, reference, reason, created_at)
                VALUES (:productId, :warehouseId, :type, :quantity, :reference, :reason, :createdAt)
                """)
                .param("productId", movement.getProductId().value())
                .param("warehouseId", movement.getWarehouseId().value())
                .param("type", movement.getType().name())
                .param("quantity", movement.getQuantity().value())
                .param("reference", movement.getReference())
                .param("reason", movement.getReason())
                .param("createdAt", movement.getCreatedAt())
                .update();
        return movement;
    }
}
