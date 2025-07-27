package com.thanh.electronicstore.exception;

public class InvalidDealException extends RuntimeException {
  public InvalidDealException(String message) {
    super(message);
  }

  public InvalidDealException(String message, Throwable cause) {
    super(message, cause);
  }
}
