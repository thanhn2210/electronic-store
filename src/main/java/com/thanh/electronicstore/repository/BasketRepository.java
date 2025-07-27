package com.thanh.electronicstore.repository;

import com.thanh.electronicstore.model.Basket;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketRepository extends JpaRepository<Basket, UUID> {}
