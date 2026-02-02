package com.board.be26.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.board.be26.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
}
