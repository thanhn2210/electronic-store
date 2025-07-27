package com.thanh.electronicstore.repository;

import com.thanh.electronicstore.model.BasketItem;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketItemRepository extends JpaRepository<BasketItem, UUID> {}
