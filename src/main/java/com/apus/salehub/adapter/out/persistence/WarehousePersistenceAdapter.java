package com.apus.salehub.adapter.out.persistence;

import com.apus.salehub.application.port.out.persistence.WarehouseRepository;
import com.apus.salehub.domain.model.warehouse.Warehouse;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WarehousePersistenceAdapter implements WarehouseRepository {

    private final JdbcClient jdbcClient;

    public WarehousePersistenceAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Warehouse> findById(WarehouseId id) {
        // TODO: Implement query
        return Optional.empty();
    }

    @Override
    public Optional<Warehouse> findByCode(String code) {
        // TODO: Implement query
        return Optional.empty();
    }

    @Override
    public List<Warehouse> findAll() {
        // TODO: Implement query
        return List.of();
    }

    @Override
    public Warehouse save(Warehouse warehouse) {
        // TODO: Implement upsert
        return warehouse;
    }

    @Override
    public void delete(WarehouseId id) {
        jdbcClient.sql("DELETE FROM warehouse WHERE id = :id")
                .param("id", id.value())
                .update();
    }
}
