package com.apus.salehub.domain.model.warehouse;

import com.apus.salehub.domain.valueobject.Address;

public class Warehouse {
    private WarehouseId id;
    private String code;
    private String name;
    private Address address;
    private boolean active;

    protected Warehouse() {}

    public Warehouse(WarehouseId id, String code, String name, Address address) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.address = address;
        this.active = true;
    }

    public WarehouseId getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public Address getAddress() { return address; }
    public boolean isActive() { return active; }

    public void deactivate() { this.active = false; }
    public void activate() { this.active = true; }
}
