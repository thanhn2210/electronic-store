package com.thanh.electronicstore.test.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thanh.electronicstore.dto.BasketItemDTO;
import com.thanh.electronicstore.model.Basket;
import com.thanh.electronicstore.model.BasketStatus;
import com.thanh.electronicstore.model.Product;
import com.thanh.electronicstore.repository.BasketRepository;
import com.thanh.electronicstore.repository.ProductRepository;
import com.thanh.electronicstore.service.BasketService;
import com.thanh.electronicstore.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private BasketService basketService;

    @Test
    void shouldAddItemsToBasketSuccessfully() {
        UUID basketId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = Product.builder()
            .id(productId)
            .name("Laptop")
            .stock(10)
            .build();

        Basket basket = Basket.builder()
            .id(basketId)
            .status(BasketStatus.ACTIVE)
            .basketItems(new ArrayList<>())
            .build();

        BasketItemDTO basketItemDTO = BasketItemDTO.builder()
            .productId(productId.toString())
            .basketId(String.valueOf(basketId))
            .quantity(2)
            .build();

        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(productService.getProductEntityById(String.valueOf(productId))).thenReturn(product);

        basketService.addBasketItems(basketId.toString(), List.of(basketItemDTO));

        // Assert
        assertEquals(8, product.getStock());
        assertEquals(1, basket.getBasketItems().size());
    }

    @Test
    void shouldFailWhenAddingItemWithInsufficientStock() {
        // Arrange
        UUID basketId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Product product = Product.builder()
            .id(productId)
            .name("Phone")
            .stock(1)
            .build();

        Basket basket = Basket.builder()
            .id(basketId)
            .status(BasketStatus.ACTIVE)
            .basketItems(new ArrayList<>())
            .build();

        BasketItemDTO basketItemDTO = BasketItemDTO.builder()
            .productId(productId.toString())
            .basketId(String.valueOf(basketId))
            .quantity(2)
            .build();

        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(productService.getProductEntityById(String.valueOf(productId))).thenReturn(product);

        basketService.addBasketItems(basketId.toString(), List.of(basketItemDTO));

        assertEquals(1, product.getStock());
    }

    @Test
    void shouldFailWhenModifyingCheckedOutBasket() {
        UUID basketId = UUID.randomUUID();

        Basket basket = Basket.builder()
            .id(basketId)
            .status(BasketStatus.CHECKED_OUT)
            .basketItems(new ArrayList<>())
            .build();

        BasketItemDTO basketItemDTO = BasketItemDTO.builder()
            .productId(UUID.randomUUID().toString())
            .basketId(String.valueOf(basketId))
            .quantity(1)
            .build();

        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            basketService.addBasketItems(basketId.toString(), List.of(basketItemDTO));
        });

        assertEquals("Cannot modify a basket that has already been checked out", exception.getMessage());
        verify(basketRepository, never()).save(any());
    }

    @Test
    void shouldRollbackAllChangesOnFailure() {
        UUID basketId = UUID.randomUUID();
        UUID product1Id = UUID.randomUUID();
        UUID product2Id = UUID.randomUUID();

        Product product1 = Product.builder()
            .id(product1Id)
            .name("Laptop")
            .stock(10)
            .build();

        Product product2 = Product.builder()
            .id(product2Id)
            .name("Phone")
            .stock(1)
            .build();

        Basket basket = Basket.builder()
            .id(basketId)
            .status(BasketStatus.ACTIVE)
            .basketItems(new ArrayList<>())
            .build();

        BasketItemDTO item1 = BasketItemDTO.builder()
            .productId(product1Id.toString())
            .quantity(2)
            .build();

        BasketItemDTO item2 = BasketItemDTO.builder()
            .productId(product2Id.toString())
            .quantity(5) // Insufficient stock
            .build();

        when(basketRepository.findById(basketId)).thenReturn(Optional.of(basket));
        when(productService.getProductEntityById(String.valueOf(product1Id))).thenReturn(product1);
        when(productService.getProductEntityById(String.valueOf(product2Id))).thenReturn(product2);

        basketService.addBasketItems(basketId.toString(), List.of(item1, item2));

        assertEquals(8, product1.getStock());
        assertEquals(1, product2.getStock());
    }
}