package com.thanh.electronicstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasketItemDTO {
    private String id;
    private String productId;
    private String basketId;
    private int quantity;
}
