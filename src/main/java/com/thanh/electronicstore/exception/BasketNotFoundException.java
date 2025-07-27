package com.thanh.electronicstore.exception;

public class BasketNotFoundException extends RuntimeException {
  public BasketNotFoundException(String basketId) {
    super("Basket not found with ID: " + basketId);
  }
}
