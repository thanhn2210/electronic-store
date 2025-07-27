package com.thanh.electronicstore.dto;

import com.thanh.electronicstore.model.ProductCategory;
import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    @Nullable
    private String id;
    private String name;
    private String description;
    private ProductCategory category;
    private BigDecimal price;
    private Integer stock;
    private Boolean available;
    private DealDTO deal;
}
