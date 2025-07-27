package com.thanh.electronicstore.service;

import com.thanh.electronicstore.model.Deal;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class DealCalculatorService {

  public BigDecimal calculateDiscount(Deal deal, BigDecimal unitPrice, int quantity) {
    if (deal == null || deal.getType() == null) return BigDecimal.valueOf(0.0);
    return deal.getType()
        .getStrategy()
        .calculateDiscount(unitPrice, quantity, deal.getDiscountValue());
  }
}
