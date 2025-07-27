package com.thanh.electronicstore.controller;

import com.thanh.electronicstore.exception.BasketAlreadyCheckedOutException;
import com.thanh.electronicstore.exception.BasketNotFoundException;
import com.thanh.electronicstore.exception.InvalidDealException;
import com.thanh.electronicstore.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({InvalidDealException.class, BasketAlreadyCheckedOutException.class})
    public ResponseEntity<String> handleInvalidDealException(InvalidDealException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({ProductNotFoundException.class, BasketNotFoundException.class})
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleGeneralRuntime(RuntimeException ex) {
        return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
    }
}
