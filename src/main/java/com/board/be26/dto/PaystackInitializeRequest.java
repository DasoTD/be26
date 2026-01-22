package com.board.be26.dto;

import java.math.BigDecimal;

public class PaystackInitializeRequest {
    private BigDecimal amount;
    private String email;
    private String reference;
    private Long orderId;

    public PaystackInitializeRequest() {}

    public PaystackInitializeRequest(BigDecimal amount, String email, String reference, Long orderId) {
        this.amount = amount;
        this.email = email;
        this.reference = reference;
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
