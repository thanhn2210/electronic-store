package com.thanh.electronicstore.service;

import java.math.BigDecimal;

public interface DealStrategy {
    BigDecimal calculateDiscount(BigDecimal unitPrice, int quantity, BigDecimal dealValue);
}
