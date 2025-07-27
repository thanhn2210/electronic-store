package com.thanh.electronicstore.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.thanh.electronicstore.dto.DealDTO;
import com.thanh.electronicstore.dto.ProductDTO;
import com.thanh.electronicstore.dto.ProductFilterCriteria;
import com.thanh.electronicstore.model.Deal;
import com.thanh.electronicstore.model.DealType;
import com.thanh.electronicstore.model.Product;
import com.thanh.electronicstore.model.ProductCategory;
import com.thanh.electronicstore.repository.DealRepository;
import com.thanh.electronicstore.repository.ProductRepository;
import com.thanh.electronicstore.service.ProductService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private DealRepository dealRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductEntityById_shouldReturnProductWhenFound() {
        UUID id = UUID.randomUUID();
        Product product = Product.builder()
            .id(id)
            .name("Test Product")
            .stock(10)
            .price(BigDecimal.valueOf(11.5))
            .category(ProductCategory.LAPTOP)
            .build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        Product actualProduct = productService.getProductEntityById(String.valueOf(id));

        assertThat(actualProduct).isEqualTo(product);
    }

    @Test
    void getProductEntityById_shouldThrowExceptionWhenNotFound() {
        UUID id = UUID.randomUUID();
        assertThrows(RuntimeException.class, () -> productService.getProductEntityById(String.valueOf(id)));
    }

    @Test
    void createProduct_shouldCreateProduct() {
        ProductDTO dto = ProductDTO.builder()
            .name("Phone")
            .category(ProductCategory.LAPTOP)
            .price(BigDecimal.valueOf(500))
            .stock(10)
            .build();

        productService.createProduct(dto);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(captor.capture());

        Product saved = captor.getValue();
        assertEquals("Phone", saved.getName());
        assertEquals(ProductCategory.LAPTOP, saved.getCategory());
        assertEquals(10, saved.getStock());
        assertEquals(BigDecimal.valueOf(500), saved.getPrice());
    }

    @Test
    void addDeal_shouldAddDealsToProduct() {
        UUID productId = UUID.randomUUID();

        Product product = Product.builder()
            .id(productId)
            .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        DealDTO dealDTO = DealDTO.builder()
            .description("Flash Sale")
            .type("PERCENTAGE_DISCOUNT")
            .expiration("2025-12-25T00:00:00")
            .build();

        productService.addDeals(List.of(dealDTO), productId.toString());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product saved = productCaptor.getValue();
        assertThat(saved.getDeals())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(
                List.of(Deal.builder()
                    .description("Flash Sale")
                    .type(DealType.PERCENTAGE_DISCOUNT)
                    .expiration(LocalDateTime.parse("2025-12-25T00:00:00"))
                    .build())
            );
    }

    @Test
    void addDeals_shouldThrowIfProductNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        DealDTO dto = DealDTO.builder()
            .description("Cyber Deal")
            .expiration("2030-12-01T00:00:00")
            .build();

        assertThrows(RuntimeException.class, () -> productService.addDeals(List.of(dto), id.toString()));
    }

    @Test
    void addDeals_shouldThrowIfDateInvalid() {
        UUID id = UUID.randomUUID();
        Product product = Product.builder().id(id).build();
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        DealDTO dto = DealDTO.builder()
            .description("Invalid Deal")
            .expiration("not-a-date")
            .build();

        assertThrows(IllegalArgumentException.class, () -> productService.addDeals(List.of(dto), id.toString()));
    }

    @Test
    void deleteProduct_shouldDeleteIfExist() {
        UUID id = UUID.randomUUID();
        when(productRepository.existsById(id)).thenReturn(true);
        productService.deleteProduct(String.valueOf(id));
        verify(productRepository).deleteById(id);
    }

    @Test
    void deleteProduct_shouldThrowExceptionIfNotExist() {
        String productId = UUID.randomUUID().toString();
        when(productRepository.existsById(UUID.fromString(productId))).thenReturn(false);
        assertThrows(RuntimeException.class, () -> productService.deleteProduct(productId));
    }

    @Test
    void filterProducts_shouldReturnFilteredProducts() {
        // Arrange
        ProductFilterCriteria criteria = ProductFilterCriteria.builder()
            .category(ProductCategory.PHONE)
            .minPrice(BigDecimal.valueOf(100))
            .maxPrice(BigDecimal.valueOf(600))
            .available(true)
            .build();

        Product p1 = Product.builder()
            .id(UUID.randomUUID())
            .name("Phone")
            .price(BigDecimal.valueOf(500))
            .category(ProductCategory.PHONE)
            .stock(10)
            .build();

        Product p2 = Product.builder()
            .id(UUID.randomUUID())
            .name("Tablet")
            .price(BigDecimal.valueOf(1000))
            .category(ProductCategory.PHONE)
            .stock(5)
            .build();
        Product p3 = Product.builder()
            .id(UUID.randomUUID())
            .name("Tablet 1")
            .price(BigDecimal.valueOf(400))
            .category(ProductCategory.PHONE)
            .stock(5)
            .build();

        List<Product> mockResult = List.of(p1, p3);
        Page<Product> mockPage = new PageImpl<>(mockResult);

        when(productRepository.findByFilter(
            eq(ProductCategory.PHONE),
            eq(BigDecimal.valueOf(100)),
            eq(BigDecimal.valueOf(600)),
            eq(true),
            any(Pageable.class))
        ).thenReturn(mockPage);

        List<ProductDTO> result = productService.filterProducts(criteria, 0, 10);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProductDTO::getName).containsExactly("Phone", "Tablet 1");

        verify(productRepository).findByFilter(
            eq(ProductCategory.PHONE),
            eq(BigDecimal.valueOf(100)),
            eq(BigDecimal.valueOf(600)),
            eq(true),
            eq(PageRequest.of(0, 10))
        );
    }
}