package com.apus.salehub.domain.exception;

import com.apus.base.response.ErrorCode;

public class StockErrorCodes {

    public static final ErrorCode STOCK_NOT_FOUND = new ErrorCode(404, "stock.not_found");

    private StockErrorCodes() {
    }
}
