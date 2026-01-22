package com.board.be26.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.be26.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByProviderReference(String providerReference);
}
