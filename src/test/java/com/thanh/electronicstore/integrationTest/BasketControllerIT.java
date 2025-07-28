package com.thanh.electronicstore.integrationTest;

import static org.assertj.core.api.Assertions.assertThat;

import com.thanh.electronicstore.dto.BasketDTO;
import com.thanh.electronicstore.dto.BasketItemDTO;
import com.thanh.electronicstore.dto.ProductDTO;
import com.thanh.electronicstore.dto.ReceiptDTO;
import com.thanh.electronicstore.model.Basket;
import com.thanh.electronicstore.model.BasketItem;
import com.thanh.electronicstore.model.BasketStatus;
import com.thanh.electronicstore.model.Product;
import com.thanh.electronicstore.model.ProductCategory;
import com.thanh.electronicstore.repository.BasketItemRepository;
import com.thanh.electronicstore.repository.BasketRepository;
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
public class BasketControllerIT {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  private String baseUrl;

  @Autowired private BasketRepository basketRepository;

  @Autowired private BasketItemRepository basketItemRepository;

  @Autowired private ProductRepository productRepository;

  private List<ProductDTO> initializedProducts;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port + "/baskets";

    basketItemRepository.deleteAll();
    basketRepository.deleteAll();
    productRepository.deleteAll();

    Product product1 =
        Product.builder()
            .name("iPhone 15")
            .category(ProductCategory.PHONE)
            .description("Newest iPhone")
            .price(BigDecimal.valueOf(999))
            .stock(50)
            .available(true)
            .build();

    Product product2 =
        Product.builder()
            .name("MacBook Pro")
            .category(ProductCategory.LAPTOP)
            .description("Apple Silicon")
            .price(BigDecimal.valueOf(1999))
            .stock(20)
            .available(true)
            .build();

    Product product3 =
        Product.builder()
            .name("iPad Air")
            .category(ProductCategory.TABLET)
            .description("Lightweight tablet")
            .price(BigDecimal.valueOf(699))
            .stock(30)
            .available(true)
            .build();
    List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2, product3));
    initializedProducts = savedProducts.stream().map(Product::toDto).toList();

    Basket basket = Basket.builder().status(BasketStatus.ACTIVE).build();
    BasketItem item1 = BasketItem.builder().product(product1).basket(basket).quantity(2).build();
    BasketItem item2 = BasketItem.builder().product(product2).basket(basket).quantity(1).build();
    basket.getBasketItems().add(item1);
    basket.getBasketItems().add(item2);
    basketRepository.save(basket);
  }

  @Test
  public void testCreateAndFetchBasket() {
    BasketDTO dto = new BasketDTO();
    dto.setStatus(BasketStatus.ACTIVE);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<BasketDTO> request = new HttpEntity<>(dto, headers);

    ResponseEntity<Void> createResp = restTemplate.postForEntity(baseUrl, request, Void.class);
    assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  public void testAddItemsToBasket_thenFetchBasket() {
    // Create basket
    BasketDTO basketDTO = new BasketDTO();
    basketDTO.setStatus(BasketStatus.ACTIVE);
    HttpEntity<BasketDTO> createReq = new HttpEntity<>(basketDTO);
    ResponseEntity<BasketDTO> createBasketResponse =
        restTemplate.postForEntity(baseUrl, createReq, BasketDTO.class);

    String basketId = createBasketResponse.getBody().getId();

    // Fetch basket ID
    ResponseEntity<BasketDTO> response =
        restTemplate.getForEntity("/baskets/{id}", BasketDTO.class, basketId);
    BasketDTO basket = response.getBody();
    assertThat(basket).isNotNull();
    assertThat(basket.getId()).isEqualTo(basketId);

    // Get product to add
    ResponseEntity<ProductDTO[]> productsResp =
        restTemplate.getForEntity("/products", ProductDTO[].class);
    ProductDTO product = productsResp.getBody()[0];

    BasketItemDTO item = new BasketItemDTO();
    item.setProductId(product.getId());
    item.setQuantity(2);
    item.setBasketId(basketId);

    HttpEntity<List<BasketItemDTO>> addItemsReq = new HttpEntity<>(List.of(item));
    ResponseEntity<BasketDTO> addResp =
        restTemplate.postForEntity(
            baseUrl + "/" + basketId + "/add-items", addItemsReq, BasketDTO.class);
    assertThat(addResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(addResp.getBody().getBasketItems()).hasSize(1);
  }

  @Test
  void testCalculateReceipt() {
    List<Basket> allBaskets = basketRepository.findAll();
    assertThat(allBaskets).hasSize(1);

    Basket basket = allBaskets.get(0);
    String basketId = basket.getId().toString();

    BigDecimal expectedTotal =
        BigDecimal.valueOf(999).multiply(BigDecimal.valueOf(2)).add(BigDecimal.valueOf(1999));

    ResponseEntity<ReceiptDTO> response =
        restTemplate.getForEntity(
            baseUrl + "/" + basketId + "/calculate-receipt", ReceiptDTO.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ReceiptDTO receipt = response.getBody();

    assertThat(receipt).isNotNull();
    assertThat(receipt.getBasketId()).isEqualTo(basketId);
    assertThat(receipt.getTotalPrice()).isEqualByComparingTo(expectedTotal);
  }

  @Test
  void testRemoveItems() {
    // Create basket
    BasketDTO basketDTO = new BasketDTO();
    basketDTO.setStatus(BasketStatus.ACTIVE);
    HttpEntity<BasketDTO> createReq = new HttpEntity<>(basketDTO);
    ResponseEntity<BasketDTO> createBasketResponse =
        restTemplate.postForEntity(baseUrl, createReq, BasketDTO.class);

    String basketId = createBasketResponse.getBody().getId();

    // Fetch basket
    ResponseEntity<BasketDTO> response =
        restTemplate.getForEntity("/baskets/{id}", BasketDTO.class, basketId);
    BasketDTO basket = response.getBody();
    assertThat(basket).isNotNull();
    assertThat(basket.getId()).isEqualTo(basketId);

    // Get product to add
    ResponseEntity<ProductDTO[]> productsResp =
        restTemplate.getForEntity("/products", ProductDTO[].class);
    ProductDTO product = productsResp.getBody()[0];

    // Add item to basket
    BasketItemDTO item = new BasketItemDTO();
    item.setProductId(product.getId());
    item.setQuantity(2);
    item.setBasketId(basketId);

    HttpEntity<List<BasketItemDTO>> addItemsReq = new HttpEntity<>(List.of(item));
    ResponseEntity<BasketDTO> addResp =
        restTemplate.postForEntity(
            baseUrl + "/" + basketId + "/add-items", addItemsReq, BasketDTO.class);
    assertThat(addResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(addResp.getBody().getBasketItems()).hasSize(1);

    // Fetch updated basket to get item IDs
    ResponseEntity<BasketDTO> fetchResp =
        restTemplate.getForEntity(baseUrl + "/" + basketId, BasketDTO.class);
    assertThat(fetchResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    BasketDTO updatedBasket = fetchResp.getBody();
    assertThat(updatedBasket).isNotNull();
    assertThat(updatedBasket.getBasketItems()).hasSize(1);

    List<String> basketItemIds =
        updatedBasket.getBasketItems().stream().map(BasketItemDTO::getId).toList();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<List<String>> deleteRequest = new HttpEntity<>(basketItemIds, headers);

    // Call remove-items
    ResponseEntity<BasketDTO> deleteResp =
        restTemplate.exchange(
            baseUrl + "/" + basketId + "/delete-items",
            HttpMethod.POST,
            deleteRequest,
            BasketDTO.class);

    assertThat(deleteResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(deleteResp.getBody()).isNotNull();
    assertThat(deleteResp.getBody().getBasketItems()).isEmpty();
  }
}
