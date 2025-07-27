package com.thanh.electronicstore.service;

import com.thanh.electronicstore.dto.DealDTO;
import com.thanh.electronicstore.model.Deal;
import com.thanh.electronicstore.model.DealType;
import com.thanh.electronicstore.repository.DealRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DealService {
  private final DealRepository dealRepository;

  public DealService(DealRepository dealRepository) {
    this.dealRepository = dealRepository;
  }

  public List<DealDTO> getAllDeals() {
    List<Deal> deals = dealRepository.findAll();
    return deals.stream().map(Deal::toDto).toList();
  }

  public void createDeal(DealDTO dealDTO) {
    Deal deal =
        Deal.builder()
            .description(dealDTO.getDescription())
            .type(DealType.valueOf(dealDTO.getType()))
            .expiration(LocalDateTime.parse(dealDTO.getExpiration()))
            .build();
    dealRepository.save(deal);
  }

  public void updateDeal(String dealId, DealDTO dealDTO) {
    Optional<Deal> dealOptional = dealRepository.findById(UUID.fromString(dealId));
    if (dealOptional.isEmpty()) {
      throw new RuntimeException("Deal is not found");
    }

    Deal deal = dealOptional.get();
    try {
      deal.setDescription(dealDTO.getDescription());
      deal.setExpiration(LocalDateTime.parse(dealDTO.getExpiration()));
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("Invalid expiration date time!");
    }

    dealRepository.save(deal);
  }
}
