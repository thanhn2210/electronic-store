package com.thanh.electronicstore.controller;

import com.thanh.electronicstore.dto.DealDTO;
import com.thanh.electronicstore.service.DealService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deals")
public class DealController {
  private final DealService dealService;

  public DealController(DealService dealService) {
    this.dealService = dealService;
  }

  @GetMapping
  public ResponseEntity<List<DealDTO>> getDeals() {
    return ResponseEntity.ok(dealService.getAllDeals());
  }

  @PostMapping("/{id}")
  public ResponseEntity<Void> updateDeal(@PathVariable String id, @RequestBody DealDTO dealDTO) {
    dealService.updateDeal(id, dealDTO);
    return ResponseEntity.ok().build();
  }
}
