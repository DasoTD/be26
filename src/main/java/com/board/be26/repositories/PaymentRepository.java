package com.board.be26.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.be26.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
