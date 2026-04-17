package com.apus.salehub.application.service;

import com.apus.salehub.application.port.in.stock.AdjustStockUseCase;
import com.apus.salehub.application.port.out.messaging.EventPublisher;
import com.apus.salehub.application.port.out.persistence.StockMovementRepository;
import com.apus.salehub.application.port.out.persistence.StockRepository;
import com.apus.salehub.domain.event.LowStockAlertEvent;
import com.apus.salehub.domain.event.StockUpdatedEvent;
import com.apus.base.exception.CommandExceptionBuilder;
import com.apus.salehub.domain.exception.StockErrorCodes;
import com.apus.salehub.domain.model.product.ProductId;
import com.apus.salehub.domain.model.stock.Stock;
import com.apus.salehub.domain.model.stock.StockMovement;
import com.apus.salehub.domain.model.warehouse.WarehouseId;
import com.apus.salehub.domain.service.InventoryDomainService;
import com.apus.salehub.domain.valueobject.Quantity;
import org.springframework.stereotype.Service;

@Service
public class AdjustStockService implements AdjustStockUseCase {

    private final StockRepository stockRepository;
    private final StockMovementRepository movementRepository;
    private final EventPublisher eventPublisher;
    private final InventoryDomainService inventoryDomainService;

    public AdjustStockService(StockRepository stockRepository,
                              StockMovementRepository movementRepository,
                              EventPublisher eventPublisher,
                              InventoryDomainService inventoryDomainService) {
        this.stockRepository = stockRepository;
        this.movementRepository = movementRepository;
        this.eventPublisher = eventPublisher;
        this.inventoryDomainService = inventoryDomainService;
    }

    @Override
    public AdjustStockResult execute(AdjustStockCommand command) {
        ProductId productId = ProductId.of(command.productId());
        WarehouseId warehouseId = WarehouseId.of(command.warehouseId());

        Stock stock = stockRepository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> CommandExceptionBuilder.exception(StockErrorCodes.STOCK_NOT_FOUND));

        Quantity previousQuantity = stock.getQuantity();
        Quantity newQuantity = Quantity.of(command.newQuantity());

        StockMovement movement = inventoryDomainService.adjustStock(stock, newQuantity, command.reason());

        stockRepository.save(stock);
        movementRepository.save(movement);

        eventPublisher.publish(StockUpdatedEvent.of(productId, warehouseId, previousQuantity, newQuantity, command.reason()));

        boolean lowStockAlert = inventoryDomainService.shouldTriggerLowStockAlert(stock);
        if (lowStockAlert) {
            eventPublisher.publish(LowStockAlertEvent.of(productId, warehouseId, stock.getQuantity(), stock.getMinimumLevel()));
        }

        return new AdjustStockResult(
                stock.getId().value(),
                previousQuantity.value(),
                newQuantity.value(),
                lowStockAlert
        );
    }
}
