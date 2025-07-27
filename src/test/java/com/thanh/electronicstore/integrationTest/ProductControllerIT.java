package com.thanh.electronicstore.integrationTest;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.thanh.electronicstore.dto.DealDTO;
import com.thanh.electronicstore.dto.ProductDTO;
import com.thanh.electronicstore.model.ProductCategory;
import com.thanh.electronicstore.repository.BasketItemRepository;
import com.thanh.electronicstore.repository.BasketRepository;
import com.thanh.electronicstore.repository.DealRepository;
import com.thanh.electronicstore.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private BasketItemRepository basketItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DealRepository dealRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/products";
    }

    @BeforeEach
    void cleanUpDatabase() {
        basketItemRepository.deleteAll();
        basketRepository.deleteAll();
        dealRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void createAndFetchProduct() {
        ProductDTO productDTO = ProductDTO.builder()
            .name("Test Phone")
            .description("Integration Test Product")
            .category(ProductCategory.PHONE)
            .price(BigDecimal.valueOf(999))
            .stock(100)
            .available(true)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProductDTO> request = new HttpEntity<>(productDTO, headers);

        // Create new product
        ResponseEntity<Void> postResponse = restTemplate.postForEntity(getBaseUrl(), request, Void.class);
        assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Fetch
        ResponseEntity<List<ProductDTO>> getResponse = restTemplate.exchange(
            getBaseUrl(),
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {}
        );

        // Assert
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProductDTO> products = getResponse.getBody();
        assertThat(products).isNotEmpty();

        ProductDTO found = products.stream()
            .filter(p -> p.getName().equals("Test Phone"))
            .findFirst()
            .orElseThrow();

        assertThat(found.getCategory()).isEqualTo(ProductCategory.PHONE);
        assertEquals(0, productDTO.getPrice().compareTo(BigDecimal.valueOf(999)));
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() {
        // Create product first
        ProductDTO productDTO = ProductDTO.builder()
            .id(null)
            .name("Phone")
            .description("Smartphone")
            .price(BigDecimal.valueOf(299.99))
            .stock(10)
            .category(ProductCategory.PHONE)
            .available(true)
            .deal(null)
            .build();
        restTemplate.postForEntity("/products", productDTO, Void.class);

        ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity("/products", ProductDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals("Phone", response.getBody()[0].getName());
    }

    @Test
    void searchProducts_shouldReturnFilteredList() {
        ProductDTO product1 = ProductDTO.builder()
            .id(null)
            .name("TV")
            .description("Smart TV")
            .price(BigDecimal.valueOf(500))
            .stock(5)
            .category(ProductCategory.PHONE)
            .available(true)
            .deal(null)
            .build();
        ProductDTO product2 = ProductDTO.builder()
            .id(null)
            .name("Fridge")
            .description("Cool fridge")
            .price(BigDecimal.valueOf(700))
            .stock(2)
            .category(ProductCategory.PHONE)
            .available(true)
            .deal(null)
            .build();
        restTemplate.postForEntity("/products", product1, Void.class);
        restTemplate.postForEntity("/products", product2, Void.class);

        String url = "/products/search?category=PHONE&minPrice=400&page=0&size=10";
        ResponseEntity<ProductDTO[]> response = restTemplate.getForEntity(url, ProductDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 1);
    }

    @Test
    void addDealToProduct_shouldUpdateProductDeal() {
        // Create product
        ProductDTO product = ProductDTO.builder()
            .name("Laptop")
            .description("Gaming")
            .price(BigDecimal.valueOf(1000))
            .stock(3)
            .category(ProductCategory.PHONE)
            .available(true)
            .deal(null)
            .build();
        restTemplate.postForEntity("/products", product, Void.class);

        // Get created product
        ResponseEntity<ProductDTO[]> getResponse = restTemplate.getForEntity("/products", ProductDTO[].class);
        ProductDTO createdProduct = getResponse.getBody()[0];
        String id = createdProduct.getId();

        // Create and add deal
        DealDTO deal = DealDTO.builder()
            .expiration("2025-07-30T00:00:00")
            .description("Summer Sale")
            .type("PERCENTAGE_DISCOUNT")
            .build();
        restTemplate.postForEntity("/products/" + id + "/add-deal", deal, String.class);

        // Validate product has updated deal
        ResponseEntity<ProductDTO[]> updated = restTemplate.getForEntity("/products", ProductDTO[].class);
        ProductDTO updatedProduct = updated.getBody()[0];

        assertNotNull(updatedProduct.getDeal());
        assertEquals("2025-07-30T00:00", updatedProduct.getDeal().getExpiration().substring(0, 16)); // giữ đúng định dạng
        assertEquals("Summer Sale", updatedProduct.getDeal().getDescription());
        assertEquals("PERCENTAGE_DISCOUNT", updatedProduct.getDeal().getType());
    }
}
