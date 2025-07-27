package com.thanh.electronicstore.service;

import com.thanh.electronicstore.dto.DealDTO;
import com.thanh.electronicstore.dto.ProductDTO;
import com.thanh.electronicstore.dto.ProductFilterCriteria;
import com.thanh.electronicstore.model.Deal;
import com.thanh.electronicstore.model.DealType;
import com.thanh.electronicstore.model.Product;
import com.thanh.electronicstore.repository.ProductRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
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

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
            .map(product -> ProductDTO.builder()
                .id(String.valueOf(product.getId()))
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(product.getCategory())
                .deal(product.getDeal() != null ? product.getDeal().toDto() : null).build()).toList();
    }

    public List<Product> getAllProductByIds(List<UUID> productIds) {
        return productRepository.findAllById(productIds);
    }

    public Product getProductEntityById(String productId) {
        return productRepository.findById(UUID.fromString(productId))
            .orElseThrow(() -> new RuntimeException("Product is not found"));
    }

    public void createProduct(ProductDTO productDTO) {
        Product newProduct = Product.builder()
            .name(productDTO.getName())
            .category(productDTO.getCategory())
            .price(productDTO.getPrice())
            .stock(productDTO.getStock())
            .build();
        productRepository.save(newProduct);
        logger.info("Product created with ID: {}", newProduct.getId());
    }

    @Transactional
    public String addDeal(DealDTO dealDTO, String productId) {
        Product existingProduct = productRepository.findById(UUID.fromString(productId)).orElseThrow(
            () -> {
                logger.error("Product not found with ID: {}", productId);
                return new RuntimeException("This product is not available");
            }
        );

        Deal deal;
        try {
            deal = Deal.builder()
                .description(dealDTO.getDescription())
                .expiration(LocalDateTime.parse(dealDTO.getExpiration()))
                .type(DealType.valueOf(dealDTO.getType()))
                .build();
        } catch (DateTimeParseException ex) {
            logger.error("Invalid expiration date format: {}", dealDTO.getExpiration());
            throw new IllegalArgumentException("Invalid expiration date format: " + dealDTO.getExpiration(), ex);
        }

        existingProduct.setDeal(deal);
        String dealId = productRepository.save(existingProduct).getDeal().getId().toString();
        logger.info("Deal added to product with ID: {}", productId);

        return dealId;
    }

    public void update(Product product) {
        if (product.getName() != null) {
            product.setName(product.getName());
        }
        if (product.getPrice() != null) {
            product.setPrice(product.getPrice());
        }
        product.setStock(product.getStock());
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(String productId) {
        UUID uuid = UUID.fromString(productId);
        if (!productRepository.existsById(uuid)) {
            logger.error("Product not found with ID: {}", productId);
            throw new RuntimeException("Product not found with ID: " + productId);
        }

        productRepository.deleteById(UUID.fromString(productId));
        logger.info("Product deleted with ID: {}", productId);
    }

    public List<ProductDTO> filterProducts(ProductFilterCriteria criteria, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByFilter(
            criteria.getCategory(),
            criteria.getMinPrice(),
            criteria.getMaxPrice(),
            criteria.getAvailable(),
            pageable
        )
            .getContent()
            .stream()
            .map(Product::toDto)
            .toList();
    }
}
