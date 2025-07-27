package com.thanh.electronicstore.dto;

import com.thanh.electronicstore.model.ProductCategory;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductFilterCriteria {
  private ProductCategory category;
  private BigDecimal minPrice;
  private BigDecimal maxPrice;
  private Boolean available;
}
