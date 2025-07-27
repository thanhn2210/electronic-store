package com.thanh.electronicstore.service;

import com.thanh.electronicstore.dto.DealDTO;
import com.thanh.electronicstore.dto.ProductDTO;
import com.thanh.electronicstore.dto.ProductFilterCriteria;
import com.thanh.electronicstore.exception.InvalidDealException;
import com.thanh.electronicstore.exception.ProductNotFoundException;
import com.thanh.electronicstore.model.Deal;
import com.thanh.electronicstore.model.DealType;
import com.thanh.electronicstore.model.Product;
import com.thanh.electronicstore.repository.DealRepository;
import com.thanh.electronicstore.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
  private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

  private final ProductRepository productRepository;
  private final DealRepository dealRepository;

  public ProductService(ProductRepository productRepository, DealRepository dealRepository) {
    this.productRepository = productRepository;
    this.dealRepository = dealRepository;
  }

  public List<ProductDTO> getAllProducts() {
    List<Product> products = productRepository.findAll();
    return products.stream()
        .map(
            product ->
                ProductDTO.builder()
                    .id(String.valueOf(product.getId()))
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .stock(product.getStock())
                    .category(product.getCategory())
                    .available(product.getAvailable())
                    .deals(
                        product.getDeals() != null
                            ? product.getDeals().stream().map(Deal::toDto).toList()
                            : null)
                    .build())
        .toList();
  }

  public ProductDTO getProductById(String id) {
    Product product =
        productRepository
            .findById(UUID.fromString(id))
            .orElseThrow(() -> new ProductNotFoundException(id));
    return product.toDto();
  }

  public List<Product> getAllProductByIds(List<UUID> productIds) {
    return productRepository.findAllById(productIds);
  }

  public Product getProductEntityById(String productId) {
    return productRepository
        .findById(UUID.fromString(productId))
        .orElseThrow(() -> new ProductNotFoundException(productId));
  }

  public void createProduct(ProductDTO productDTO) {
    Product newProduct =
        Product.builder()
            .name(productDTO.getName())
            .category(productDTO.getCategory())
            .price(productDTO.getPrice())
            .stock(productDTO.getStock())
            .build();
    productRepository.save(newProduct);
    logger.info("Product created with ID: {}", newProduct.getId());
  }

  @Transactional
  public void addDeals(List<DealDTO> dealDTOs, String productId) {
    UUID productUUID = UUID.fromString(productId);
    Product product =
        productRepository
            .findById(productUUID)
            .orElseThrow(
                () -> {
                  logger.error("Product not found with ID: {}", productId);
                  return new ProductNotFoundException(productId);
                });

    List<Deal> deals = new ArrayList<>();
    for (DealDTO dto : dealDTOs) {
      try {
        Deal deal =
            Deal.builder()
                .description(dto.getDescription())
                .expiration(LocalDateTime.parse(dto.getExpiration()))
                .type(DealType.valueOf(dto.getType()))
                .build();
        deals.add(deal);
      } catch (DateTimeParseException | IllegalArgumentException e) {
        logger.error("Failed to parse deal DTO: {}", dto, e);
        throw new InvalidDealException("Invalid expiration date format: " + dto.getExpiration(), e);
      }
    }

    dealRepository.saveAll(deals);
    if (product.getDeals() == null) {
      product.setDeals(new ArrayList<>());
    }
    product.getDeals().addAll(deals);
    productRepository.save(product);

    logger.info("Successfully added {} deal(s) to product ID: {}", deals.size(), productId);
  }

  @Transactional
  public void deleteProduct(String productId) {
    UUID uuid = UUID.fromString(productId);
    if (!productRepository.existsById(uuid)) {
      logger.error("Product not found with ID: {}", productId);
      throw new ProductNotFoundException(productId);
    }

    productRepository.deleteById(UUID.fromString(productId));
    logger.info("Product deleted with ID: {}", productId);
  }

  public List<ProductDTO> filterProducts(ProductFilterCriteria criteria, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return productRepository
        .findByFilter(
            criteria.getCategory(),
            criteria.getMinPrice(),
            criteria.getMaxPrice(),
            criteria.getAvailable(),
            pageable)
        .getContent()
        .stream()
        .map(Product::toDto)
        .toList();
  }
}
