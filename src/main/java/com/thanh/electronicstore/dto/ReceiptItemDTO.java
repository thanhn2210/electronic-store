package com.thanh.electronicstore.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiptItemDTO {
    private String productName;
    private int quantity;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal finalPrice;
}