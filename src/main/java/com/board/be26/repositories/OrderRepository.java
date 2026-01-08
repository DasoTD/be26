package com.board.be26.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.be26.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
