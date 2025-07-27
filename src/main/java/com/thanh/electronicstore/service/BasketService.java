package com.thanh.electronicstore.service;

import com.thanh.electronicstore.dto.BasketDTO;
import com.thanh.electronicstore.dto.BasketItemDTO;
import com.thanh.electronicstore.dto.ReceiptDTO;
import com.thanh.electronicstore.dto.ReceiptItemDTO;
import com.thanh.electronicstore.model.Basket;
import com.thanh.electronicstore.model.BasketItem;
import com.thanh.electronicstore.model.BasketStatus;
import com.thanh.electronicstore.model.Product;
import com.thanh.electronicstore.repository.BasketRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.antlr.v4.runtime.misc.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BasketService {
    private static final Logger logger = LoggerFactory.getLogger(BasketService.class);

    private final BasketRepository basketRepository;
    private final ProductService productService;
    private final DealCalculatorService dealCalculatorService;

    public BasketService(BasketRepository basketRepository, ProductService productService,
        DealCalculatorService dealCalculatorService) {
        this.basketRepository = basketRepository;
        this.productService = productService;
        this.dealCalculatorService = dealCalculatorService;
    }

    public BasketDTO getBasket(String id) {
        Basket basket = basketRepository.findById(UUID.fromString(id))
            .orElseThrow(() -> new RuntimeException("Basket is not found"));
        return basket.toDto();
    }

    public String createBasket(BasketDTO basketDTO) {
        Basket basket = new Basket();
        basket.setStatus(BasketStatus.ACTIVE);

        List<UUID> productIds = basketDTO.getBasketItems().stream()
            .map(dto -> UUID.fromString(dto.getProductId()))
            .toList();
        Map<UUID, Product> productMap = productService.getAllProductByIds(productIds).stream().collect(
            Collectors.toMap(Product::getId, Function.identity()));

        List<BasketItem> basketItems = basketDTO.getBasketItems().stream().map(
            basketItemDTO -> {
                Product product = productMap.get(UUID.fromString(basketItemDTO.getProductId()));
                if (product == null) {
                    throw new IllegalArgumentException("Product not found for id: " + basketItemDTO.getProductId());
                }
                return BasketItem.builder()
                    .product(product)
                    .quantity(basketItemDTO.getQuantity())
                    .basket(basket)
                    .build();
            }
        ).toList();

        basket.setBasketItems(basketItems);

        return String.valueOf(basketRepository.save(basket).getId());
    }

    @Transactional
    public Pair<List<BasketItemDTO>, List<BasketItemDTO>> addBasketItems(String basketId, List<BasketItemDTO> addBasketItems) {
        Basket basket = basketRepository.findById(UUID.fromString(basketId))
            .orElseThrow(() -> new RuntimeException("Basket is not found"));

        if (basket.getStatus() != BasketStatus.ACTIVE) {
            throw new RuntimeException("Cannot modify a basket that has already been checked out");
        }

        List<BasketItemDTO> skippedItems = new ArrayList<>();
        List<BasketItemDTO> addedItems = new ArrayList<>();

        for (BasketItemDTO basketItemDTO : addBasketItems) {
            Product product = productService.getProductEntityById(basketItemDTO.getProductId());

            if (product.getStock() < basketItemDTO.getQuantity()) {
                skippedItems.add(basketItemDTO);
                continue;
            }

            product.setStock(product.getStock() - basketItemDTO.getQuantity());
            addedItems.add(basketItemDTO);

            BasketItem basketItem = BasketItem.builder()
                .product(product)
                .basket(basket)
                .quantity(basketItemDTO.getQuantity())
                .build();

            basket.getBasketItems().add(basketItem);
        }

        basketRepository.saveAndFlush(basket);

        return new Pair<>(skippedItems, addedItems);
    }


    public void removeBasketItems(String basketId, List<String> removedBasketItemIds) {
        Optional<Basket> basketOptional = basketRepository.findById(UUID.fromString(basketId));
        if (basketOptional.isEmpty()) {
            throw new RuntimeException("Basket is not found");
        }

        Basket basket = basketOptional.get();
        basket.getBasketItems().removeIf(basketItem -> removedBasketItemIds.contains(basketItem.getId().toString()));
        basketRepository.save(basket);
    }

    public ReceiptDTO calculateReceipt(String basketId) {
        Optional<Basket> basketOptional = basketRepository.findById(UUID.fromString(basketId));
        if (basketOptional.isEmpty()) {
            throw new RuntimeException("Basket not found");
        }

        Basket basket = basketOptional.get();
        List<ReceiptItemDTO> receiptItems = basket.getBasketItems().stream().map(basketItem -> {
            Product product = basketItem.getProduct();
            BigDecimal originalPrice = product.getPrice().multiply(BigDecimal.valueOf(basketItem.getQuantity()));
            BigDecimal discount = dealCalculatorService.calculateDiscount(product.getDeal(), product.getPrice(), basketItem.getQuantity());
            BigDecimal finalPrice = originalPrice.subtract(discount);

            return ReceiptItemDTO.builder()
                .productName(product.getName())
                .quantity(basketItem.getQuantity())
                .originalPrice(originalPrice)
                .discount(discount)
                .finalPrice(finalPrice)
                .build();
        }).toList();

        BigDecimal totalPrice = receiptItems.stream()
            .map(ReceiptItemDTO::getFinalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReceiptDTO.builder()
            .basketId(basketId)
            .items(receiptItems)
            .totalPrice(totalPrice)
            .build();
    }
}
