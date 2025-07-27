package com.thanh.electronicstore.controller;

import com.thanh.electronicstore.dto.DealDTO;
import com.thanh.electronicstore.dto.ProductDTO;
import com.thanh.electronicstore.dto.ProductFilterCriteria;
import com.thanh.electronicstore.service.ProductService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody ProductDTO productDTO) {
        productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}/add-deals")
    public ResponseEntity<Void> addDeal(@PathVariable String id, @RequestBody List<DealDTO> dealDTOs) {
        productService.addDeals(dealDTOs, id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
        @ModelAttribute ProductFilterCriteria criteria,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        List<ProductDTO> products = productService.filterProducts(criteria, page, size);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
