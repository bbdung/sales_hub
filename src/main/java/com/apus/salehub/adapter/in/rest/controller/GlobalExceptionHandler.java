package com.apus.salehub.adapter.in.rest.controller;

import com.apus.base.exception.CentralizeExceptionHandler;
import com.apus.base.service.ResponseService;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends CentralizeExceptionHandler {

    public GlobalExceptionHandler(ResponseService responseService) {
        super(responseService);
    }
}
