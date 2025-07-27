package com.thanh.electronicstore.model;

import com.thanh.electronicstore.dto.ProductDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
  @Id @GeneratedValue private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private ProductCategory category;

  @Column(nullable = false)
  private BigDecimal price;

  @ManyToMany(cascade = CascadeType.PERSIST)
  @JoinTable(
      name = "product_deal",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "deal_id"))
  private List<Deal> deals;

  private int stock;
  private Boolean available;
  private String description;

  public ProductDTO toDto() {
    return ProductDTO.builder()
        .id(this.id.toString())
        .name(this.name)
        .category(this.category)
        .price(this.price)
        .stock(this.stock)
        .available(this.available)
        .description(this.description)
        .deals(this.deals != null ? this.deals.stream().map(Deal::toDto).toList() : null)
        .build();
  }
}
