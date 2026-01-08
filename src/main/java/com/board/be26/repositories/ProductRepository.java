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

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
}
