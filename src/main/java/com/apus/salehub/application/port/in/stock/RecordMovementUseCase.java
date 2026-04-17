package com.apus.salehub.application.port.in.stock;

import java.time.Instant;

public interface RecordMovementUseCase {

    record RecordMovementCommand(
        Long productId,
        Long warehouseId,
        String movementType,  // IN, OUT, TRANSFER, ADJUSTMENT
        int quantity,
        String reference,
        String reason
    ) {}

    record RecordMovementResult(
        Long movementId,
        Long productId,
        Long warehouseId,
        String movementType,
        int quantity,
        Instant recordedAt
    ) {}

    RecordMovementResult execute(RecordMovementCommand command);
}
