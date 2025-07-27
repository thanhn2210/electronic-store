package com.thanh.electronicstore.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageDiscountStrategy implements DealStrategy {

  @Override
  public BigDecimal calculateDiscount(BigDecimal unitPrice, int quantity, BigDecimal dealValue) {
    if (unitPrice == null || dealValue == null || quantity <= 0) {
      return BigDecimal.ZERO;
    }

    BigDecimal discountRate = dealValue.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

    return totalPrice.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
  }
}
