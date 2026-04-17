package com.apus.salehub.application.service;

import com.apus.base.exception.CommandExceptionBuilder;
import com.apus.salehub.application.port.in.stock.CheckStockUseCase;
import com.apus.salehub.application.port.out.persistence.StockRepository;
import com.apus.salehub.domain.exception.StockErrorCodes;
import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.stock.Stock;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import org.springframework.stereotype.Service;

@Service
public class CheckStockService implements CheckStockUseCase {

    private final StockRepository stockRepository;

    public CheckStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public CheckStockResult execute(CheckStockQuery query) {
        ProductId productId = ProductId.of(query.productId());
        WarehouseId warehouseId = WarehouseId.of(query.warehouseId());

        Stock stock = stockRepository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> CommandExceptionBuilder.exception(StockErrorCodes.STOCK_NOT_FOUND));

        return new CheckStockResult(
                stock.getId().value(),
                stock.getProductId().value(),
                stock.getWarehouseId().value(),
                stock.getQuantity().value(),
                stock.getReservedQuantity().value(),
                stock.getAvailableQuantity().value(),
                stock.isLowStock()
        );
    }
}
