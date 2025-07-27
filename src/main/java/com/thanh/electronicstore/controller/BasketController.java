package com.thanh.electronicstore.controller;

import com.thanh.electronicstore.dto.BasketDTO;
import com.thanh.electronicstore.dto.BasketItemDTO;
import com.thanh.electronicstore.dto.ReceiptDTO;
import com.thanh.electronicstore.service.BasketService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/baskets")
public class BasketController {
  private final BasketService basketService;

  public BasketController(BasketService basketService) {
    this.basketService = basketService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<BasketDTO> getBasket(@PathVariable String id) {
    return ResponseEntity.ok(basketService.getBasket(id));
  }

  @PostMapping
  public ResponseEntity<String> createBasket(@RequestBody BasketDTO basketDTO) {
    String createdBasketId = basketService.createBasket(basketDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdBasketId);
  }

  @PostMapping("/{id}/add-items")
  public ResponseEntity<Void> addItems(
      @PathVariable String id, @RequestBody List<BasketItemDTO> basketItemDTOs) {
    basketService.addBasketItems(id, basketItemDTOs);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping("/{id}/delete-items")
  public ResponseEntity<Void> removeItems(
      @PathVariable String id, @RequestBody List<String> basketItemIds) {
    basketService.removeBasketItems(id, basketItemIds);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/{id}/calculate-receipt")
  public ResponseEntity<ReceiptDTO> calculateReceipt(@PathVariable String id) {
    return ResponseEntity.ok(basketService.calculateReceipt(id));
  }
}
