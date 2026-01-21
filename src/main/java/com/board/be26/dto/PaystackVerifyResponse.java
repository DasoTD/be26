package com.board.be26.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaystackVerifyResponse {
    private boolean status;
    private String message;
    private PaystackVerifyData data;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PaystackVerifyData getData() {
        return data;
    }

    public void setData(PaystackVerifyData data) {
        this.data = data;
    }

    public static class PaystackVerifyData {
        private Integer id;
        private String reference;
        private String status;
        private Integer amount;
        private String customer_email;
        private Long paid_at;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

        public String getCustomer_email() {
            return customer_email;
        }

        public void setCustomer_email(String customer_email) {
            this.customer_email = customer_email;
        }

        public Long getPaid_at() {
            return paid_at;
        }

        public void setPaid_at(Long paid_at) {
            this.paid_at = paid_at;
        }
    }
}
