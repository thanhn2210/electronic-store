package com.thanh.electronicstore.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiptDTO {
    private String basketId;
    private List<ReceiptItemDTO> items;
    private BigDecimal totalPrice;
}