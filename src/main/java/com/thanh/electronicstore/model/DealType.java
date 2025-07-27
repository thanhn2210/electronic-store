package com.thanh.electronicstore.model;

import com.thanh.electronicstore.service.DealStrategy;
import com.thanh.electronicstore.service.FixedAmountDiscountStrategy;
import com.thanh.electronicstore.service.PercentageDiscountStrategy;

public enum DealType {
    PERCENTAGE_DISCOUNT(new PercentageDiscountStrategy()),
    FIXED_AMOUNT_DISCOUNT(new FixedAmountDiscountStrategy());

    private final DealStrategy strategy;

    DealType(DealStrategy strategy) {
        this.strategy = strategy;
    }

    public DealStrategy getStrategy() {
        return strategy;
    }
}

