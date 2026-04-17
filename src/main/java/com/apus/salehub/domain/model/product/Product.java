package com.apus.salehub.domain.model.product;

import com.apus.salehub.domain.valueobject.Money;

public class Product {
    private ProductId id;
    private String sku;
    private String name;
    private String description;
    private Money unitPrice;
    private String category;
    private boolean active;

    protected Product() {}

    public Product(ProductId id, String sku, String name, String description,
                   Money unitPrice, String category) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.category = category;
        this.active = true;
    }

    public ProductId getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Money getUnitPrice() { return unitPrice; }
    public String getCategory() { return category; }
    public boolean isActive() { return active; }

    public void deactivate() { this.active = false; }
    public void activate() { this.active = true; }

    public void updatePrice(Money newPrice) { this.unitPrice = newPrice; }
}
