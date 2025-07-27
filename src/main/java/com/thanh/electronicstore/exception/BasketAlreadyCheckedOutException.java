package com.thanh.electronicstore.exception;

public class BasketAlreadyCheckedOutException extends RuntimeException {
  public BasketAlreadyCheckedOutException(String basketId) {
    super("Basket with ID " + basketId + " has already been checked out.");
  }
}
