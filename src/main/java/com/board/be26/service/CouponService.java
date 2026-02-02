package com.board.be26.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.be26.dto.CouponResponse;
import com.board.be26.dto.CreateCouponRequest;
import com.board.be26.entity.Coupon;
import com.board.be26.entity.CouponType;
import com.board.be26.repositories.CouponRepository;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Transactional
    public CouponResponse createCoupon(CreateCouponRequest request) {
        String code = normalizeCode(request.getCode());
        couponRepository.findByCode(code)
                .ifPresent(c -> { throw new IllegalArgumentException("Coupon code already exists"); });

        validateCouponValue(request.getType(), request.getValue());

        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setType(request.getType());
        coupon.setValue(request.getValue().setScale(2, RoundingMode.HALF_UP));
        coupon.setStartsAt(request.getStartsAt());
        coupon.setEndsAt(request.getEndsAt());
        coupon.setUsageLimit(request.getUsageLimit());
        if (request.getActive() != null) {
            coupon.setActive(request.getActive());
        }

        return toResponse(couponRepository.save(coupon));
    }

    @Transactional(readOnly = true)
    public List<CouponResponse> listCoupons() {
        return couponRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CouponResponse getByCode(String code) {
        Coupon coupon = couponRepository.findByCode(normalizeCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        return toResponse(coupon);
    }

    @Transactional
    public CouponResponse deactivate(String code) {
        Coupon coupon = couponRepository.findByCode(normalizeCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        coupon.setActive(false);
        return toResponse(couponRepository.save(coupon));
    }

    @Transactional
    public DiscountResult applyCoupon(String code, BigDecimal subtotal) {
        Coupon coupon = couponRepository.findByCode(normalizeCode(code))
                .orElseThrow(() -> new IllegalArgumentException("Invalid coupon code"));

        validateCouponAvailability(coupon);
        validateCouponValue(coupon.getType(), coupon.getValue());

        BigDecimal discountAmount = calculateDiscount(coupon, subtotal);
        BigDecimal finalTotal = subtotal.subtract(discountAmount).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        return new DiscountResult(coupon, discountAmount, finalTotal);
    }

    private void validateCouponAvailability(Coupon coupon) {
        if (!coupon.isActive()) {
            throw new IllegalStateException("Coupon is inactive");
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartsAt() != null && now.isBefore(coupon.getStartsAt())) {
            throw new IllegalStateException("Coupon is not yet active");
        }
        if (coupon.getEndsAt() != null && now.isAfter(coupon.getEndsAt())) {
            throw new IllegalStateException("Coupon has expired");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new IllegalStateException("Coupon usage limit reached");
        }
    }

    private void validateCouponValue(CouponType type, BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Coupon value must be greater than 0");
        }
        if (type == CouponType.PERCENTAGE && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage coupon cannot exceed 100");
        }
    }

    private BigDecimal calculateDiscount(Coupon coupon, BigDecimal subtotal) {
        if (coupon.getType() == CouponType.PERCENTAGE) {
            return subtotal.multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return coupon.getValue().min(subtotal).setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeCode(String code) {
        return code == null ? null : code.trim().toUpperCase();
    }

    private CouponResponse toResponse(Coupon coupon) {
        CouponResponse resp = new CouponResponse();
        resp.setId(coupon.getId());
        resp.setCode(coupon.getCode());
        resp.setType(coupon.getType());
        resp.setValue(coupon.getValue());
        resp.setActive(coupon.isActive());
        resp.setStartsAt(coupon.getStartsAt());
        resp.setEndsAt(coupon.getEndsAt());
        resp.setUsageLimit(coupon.getUsageLimit());
        resp.setUsedCount(coupon.getUsedCount());
        resp.setCreatedAt(coupon.getCreatedAt());
        resp.setUpdatedAt(coupon.getUpdatedAt());
        return resp;
    }

    public static class DiscountResult {
        private final Coupon coupon;
        private final BigDecimal discountAmount;
        private final BigDecimal finalTotal;

        public DiscountResult(Coupon coupon, BigDecimal discountAmount, BigDecimal finalTotal) {
            this.coupon = coupon;
            this.discountAmount = discountAmount;
            this.finalTotal = finalTotal;
        }

        public Coupon getCoupon() {
            return coupon;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public BigDecimal getFinalTotal() {
            return finalTotal;
        }
    }
}
