package com.apus.salehub.adapter.out.persistence;

import com.apus.salehub.domain.valueobject.Quantity;
import com.apus.salehub.application.port.out.persistence.StockRepository;
import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.stock.Stock;
import com.apus.salehub.domain.model.stock.StockId;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Persistence adapter using JdbcClient.
 * <p>
 * Tenant isolation is handled at the database level via TenantRoutingDataSource.
 * Each tenant has its own database, and this service's schema is set via
 * the JDBC URL's currentSchema parameter. No tenant_id column filtering needed.
 */
@Repository
public class StockPersistenceAdapter implements StockRepository {

    private final JdbcClient jdbcClient;

    public StockPersistenceAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private static final RowMapper<Stock> ROW_MAPPER = (rs, rowNum) -> Stock.reconstitute(
            new StockId(rs.getLong("id")),
            new ProductId(rs.getLong("product_id")),
            new WarehouseId(rs.getLong("warehouse_id")),
            Quantity.of(rs.getInt("quantity")),
            Quantity.of(rs.getInt("reserved_quantity")),
            Quantity.of(rs.getInt("minimum_level"))
    );

    @Override
    public Optional<Stock> findById(StockId id) {
        return jdbcClient.sql("SELECT * FROM stock WHERE id = :id")
                .param("id", id.value())
                .query(ROW_MAPPER)
                .optional();
    }

    @Override
    public Optional<Stock> findByProductAndWarehouse(ProductId productId, WarehouseId warehouseId) {
        return jdbcClient.sql("""
                SELECT * FROM stock
                WHERE product_id = :productId AND warehouse_id = :warehouseId
                """)
                .param("productId", productId.value())
                .param("warehouseId", warehouseId.value())
                .query(ROW_MAPPER)
                .optional();
    }

    @Override
    public List<Stock> findByProduct(ProductId productId) {
        return jdbcClient.sql("SELECT * FROM stock WHERE product_id = :productId")
                .param("productId", productId.value())
                .query(ROW_MAPPER)
                .list();
    }

    @Override
    public List<Stock> findByWarehouse(WarehouseId warehouseId) {
        return jdbcClient.sql("SELECT * FROM stock WHERE warehouse_id = :warehouseId")
                .param("warehouseId", warehouseId.value())
                .query(ROW_MAPPER)
                .list();
    }

    @Override
    public List<Stock> findLowStock() {
        return jdbcClient.sql("SELECT * FROM stock WHERE quantity <= minimum_level")
                .query(ROW_MAPPER)
                .list();
    }

    @Override
    public Stock save(Stock stock) {
        jdbcClient.sql("""
                INSERT INTO stock (product_id, warehouse_id, quantity, reserved_quantity, minimum_level)
                VALUES (:productId, :warehouseId, :quantity, :reservedQuantity, :minimumLevel)
                ON CONFLICT (product_id, warehouse_id) DO UPDATE SET
                    quantity = :quantity,
                    reserved_quantity = :reservedQuantity,
                    minimum_level = :minimumLevel
                """)
                .param("productId", stock.getProductId().value())
                .param("warehouseId", stock.getWarehouseId().value())
                .param("quantity", stock.getQuantity().value())
                .param("reservedQuantity", stock.getReservedQuantity().value())
                .param("minimumLevel", stock.getMinimumLevel().value())
                .update();
        return stock;
    }

    @Override
    public void delete(StockId id) {
        jdbcClient.sql("DELETE FROM stock WHERE id = :id")
                .param("id", id.value())
                .update();
    }
}
