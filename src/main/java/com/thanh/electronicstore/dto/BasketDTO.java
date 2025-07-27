package com.thanh.electronicstore.dto;

import com.thanh.electronicstore.model.BasketStatus;
import java.util.ArrayList;
import java.util.List;
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
public class BasketDTO {
    private String id;
    @Builder.Default
    private List<BasketItemDTO> basketItems = new ArrayList<>();
    private BasketStatus status;
}
