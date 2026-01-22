package com.board.be26.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.board.be26.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByProviderReference(String providerReference);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.order.user.id = :userId")
    Long countByOrder_User_Id(@Param("userId") Long userId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.order.user.id = :userId AND p.status = 'SUCCEEDED'")
    BigDecimal getTotalSpentByUserId(@Param("userId") Long userId);
}
