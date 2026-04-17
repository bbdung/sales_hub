package com.apus.salehub.application.port.out.persistence;

import com.apus.salehub.domain.model.product.Product;
import com.apus.salehub.domain.model.product.ProductId;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(ProductId id);
    Optional<Product> findBySku(String sku);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    Product save(Product product);
    void delete(ProductId id);
}
