package com.thanh.electronicstore.model;

import com.thanh.electronicstore.dto.DealDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;

@Entity
@Table(name = "deal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deal {
    @Id
    @GeneratedValue
    private UUID id;
    private String description;
    private LocalDateTime expiration;
    @Enumerated(EnumType.STRING)
    private DealType type;
    private BigDecimal discountValue;


    public DealDTO toDto() {
        return DealDTO.builder()
            .id(this.id.toString())
            .description(this.description)
            .expiration(this.expiration.toString())
            .type(this.type.name())
            .discountValue(discountValue)
            .build();
    }
}
