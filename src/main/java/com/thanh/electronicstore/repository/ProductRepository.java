package com.thanh.electronicstore.repository;

import com.thanh.electronicstore.model.Product;
import com.thanh.electronicstore.model.ProductCategory;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    @Query("SELECT p FROM Product p WHERE "
        + "(:category IS NULL OR p.category = :category) AND "
        + "(:minPrice IS NULL OR p.price >= :minPrice) AND "
        + "(:maxPrice IS NULL OR p.price <= :maxPrice) AND "
        + "(:available IS NULL OR p.available = :available)")
    Page<Product> findByFilter(
        @Param("category") ProductCategory category,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("available") Boolean available,
        Pageable pageable
    );
}
