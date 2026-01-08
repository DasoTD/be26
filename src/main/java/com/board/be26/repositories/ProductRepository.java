package com.board.be26.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.board.be26.entity.Product;
import com.board.be26.entity.ProductStatus;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name = :name")
    Optional<Product> findByName(String name);

    Optional<Product> findBySku(String sku);

    @Query("SELECT p FROM Product p WHERE (:status IS NULL OR p.status = :status) " +
           "AND (:category IS NULL OR LOWER(p.category) = LOWER(:category)) " +
           "AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> search(ProductStatus status, String category, String name, Pageable pageable);
}
