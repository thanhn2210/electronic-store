package com.thanh.electronicstore.model;

import com.thanh.electronicstore.dto.BasketItemDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "basket_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BasketItem {
  @Id @GeneratedValue private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "basket_id")
  private Basket basket;

  private int quantity;

  public BasketItemDTO toDto() {
    return BasketItemDTO.builder()
        .id(id.toString())
        .productId(product.getId().toString())
        .basketId(basket.getId().toString())
        .quantity(quantity)
        .build();
  }
}
