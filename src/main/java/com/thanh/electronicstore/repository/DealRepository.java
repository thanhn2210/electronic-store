package com.thanh.electronicstore.repository;

import com.thanh.electronicstore.model.Deal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealRepository extends JpaRepository<Deal, UUID> {}
