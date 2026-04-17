package com.apus.salehub.adapter.in.rest.controller;

import com.apus.base.response.ErrorCode;
import com.apus.base.response.ResponseWrapper;
import com.apus.salehub.adapter.in.rest.dto.AdjustStockRequest;
import com.apus.salehub.adapter.in.rest.dto.CheckStockResponse;
import com.apus.salehub.adapter.in.rest.dto.StockResponse;
import com.apus.salehub.application.port.in.stock.AdjustStockUseCase;
import com.apus.salehub.application.port.in.stock.AdjustStockUseCase.AdjustStockCommand;
import com.apus.salehub.application.port.in.stock.AdjustStockUseCase.AdjustStockResult;
import com.apus.salehub.application.port.in.stock.CheckStockUseCase;
import com.apus.salehub.application.port.in.stock.CheckStockUseCase.CheckStockQuery;
import com.apus.salehub.application.port.in.stock.CheckStockUseCase.CheckStockResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stocks")
@Tag(name = "Stock", description = "Stock management operations")
@ResponseWrapper
public class StockController {

    private final CheckStockUseCase checkStockUseCase;
    private final AdjustStockUseCase adjustStockUseCase;

    public StockController(CheckStockUseCase checkStockUseCase, AdjustStockUseCase adjustStockUseCase) {
        this.checkStockUseCase = checkStockUseCase;
        this.adjustStockUseCase = adjustStockUseCase;
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    // @PreAuthorize("hasRole('CASHIER')")
    @Operation(
            summary = "Check stock level",
            description = "Returns current stock level for a product in a specific warehouse. Requires CASHIER role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stock level retrieved"),
                    @ApiResponse(responseCode = "404", description = "Stock not found",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient role",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class)))
            }
    )
    public CheckStockResponse checkStock(@PathVariable Long productId, @PathVariable Long warehouseId) {
        CheckStockResult result = checkStockUseCase.execute(new CheckStockQuery(productId, warehouseId));
        return CheckStockResponse.from(result);
    }

    @PostMapping("/adjust")
    // @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Adjust stock level",
            description = "Adjusts the stock level for a product in a specific warehouse. Requires MANAGER role.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Stock adjusted"),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class))),
                    @ApiResponse(responseCode = "404", description = "Stock not found",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class))),
                    @ApiResponse(responseCode = "403", description = "Insufficient role",
                            content = @Content(schema = @Schema(implementation = ErrorCode.class)))
            }
    )
    public StockResponse adjustStock(@RequestBody @jakarta.validation.Valid AdjustStockRequest request) {
        AdjustStockCommand command = new AdjustStockCommand(
                request.productId(),
                request.warehouseId(),
                request.newQuantity(),
                request.reason()
        );
        AdjustStockResult result = adjustStockUseCase.execute(command);
        return StockResponse.from(result);
    }
}
