package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.warehouse.Warehouse;
import com.apus.salehub.domain.model.warehouse.WarehouseId;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository {
    Optional<Warehouse> findById(WarehouseId id);
    Optional<Warehouse> findByCode(String code);
    List<Warehouse> findAll();
    Warehouse save(Warehouse warehouse);
    void delete(WarehouseId id);
}
