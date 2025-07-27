package com.thanh.electronicstore.dto;

import java.math.BigDecimal;
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
public class DealDTO {
  private String id;
  private String description;
  private String expiration;
  private String type;
  private BigDecimal discountValue;
}
