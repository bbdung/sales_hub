package com.apus.salehub.adapter.in.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdjustStockRequest(
    @NotNull Long productId,
    @NotNull Long warehouseId,
    @Min(0) int newQuantity,
    @NotBlank String reason
) {}
