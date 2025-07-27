package com.thanh.electronicstore.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FixedAmountDiscountStrategy implements DealStrategy {

    @Override
    public BigDecimal calculateDiscount(BigDecimal unitPrice, int quantity, BigDecimal dealValue) {
        if (unitPrice == null || dealValue == null || quantity <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDiscount = dealValue.multiply(BigDecimal.valueOf(quantity));
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return totalDiscount.min(totalPrice).setScale(2, RoundingMode.HALF_UP);
    }
}
