package com.apus.salehub.adapter.out.persistence;

import com.apus.salehub.application.port.out.persistence.ProductRepository;
import com.apus.salehub.domain.model.product.Product;
import com.apus.salehub.domain.model.product.ProductId;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductPersistenceAdapter implements ProductRepository {

    private final JdbcClient jdbcClient;

    public ProductPersistenceAdapter(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        // TODO: Implement query
        return Optional.empty();
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        // TODO: Implement query
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        // TODO: Implement query
        return List.of();
    }

    @Override
    public List<Product> findByCategory(String category) {
        // TODO: Implement query
        return List.of();
    }

    @Override
    public Product save(Product product) {
        // TODO: Implement upsert
        return product;
    }

    @Override
    public void delete(ProductId id) {
        jdbcClient.sql("DELETE FROM product WHERE id = :id")
                .param("id", id.value())
                .update();
    }
}
