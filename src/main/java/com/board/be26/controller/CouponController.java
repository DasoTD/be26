package com.board.be26.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.be26.dto.CouponResponse;
import com.board.be26.dto.CreateCouponRequest;
import com.board.be26.service.CouponService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        CouponResponse created = couponService.createCoupon(request);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CouponResponse>> listCoupons() {
        return ResponseEntity.ok(couponService.listCoupons());
    }

    @GetMapping("/{code}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable String code) {
        return ResponseEntity.ok(couponService.getByCode(code));
    }

    @PostMapping("/{code}/deactivate")
    public ResponseEntity<CouponResponse> deactivate(@PathVariable String code) {
        return ResponseEntity.ok(couponService.deactivate(code));
    }
}
