package com.board.be26.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlutterwaveVerifyResponse {
    
    private boolean status;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private FlutterwaveTransactionData data;
    
    public FlutterwaveVerifyResponse() {}
    
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
    
    public FlutterwaveTransactionData getData() {
        return data;
    }
    
    public void setData(FlutterwaveTransactionData data) {
        this.data = data;
    }
    
    /**
     * Nested class representing Flutterwave transaction verification data
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlutterwaveTransactionData {
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("tx_ref")
        private String txRef;
        
        @JsonProperty("flw_ref")
        private String flwRef;
        
        @JsonProperty("amount")
        private Double amount;
        
        @JsonProperty("currency")
        private String currency;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("payment_type")
        private String paymentType;
        
        @JsonProperty("created_at")
        private String createdAt;
        
        @JsonProperty("customer")
        private Customer customer;
        
        @JsonProperty("processor_response")
        private String processorResponse;
        
        public FlutterwaveTransactionData() {}
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getTxRef() {
            return txRef;
        }
        
        public void setTxRef(String txRef) {
            this.txRef = txRef;
        }
        
        public String getFlwRef() {
            return flwRef;
        }
        
        public void setFlwRef(String flwRef) {
            this.flwRef = flwRef;
        }
        
        public Double getAmount() {
            return amount;
        }
        
        public void setAmount(Double amount) {
            this.amount = amount;
        }
        
        public String getCurrency() {
            return currency;
        }
        
        public void setCurrency(String currency) {
            this.currency = currency;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getPaymentType() {
            return paymentType;
        }
        
        public void setPaymentType(String paymentType) {
            this.paymentType = paymentType;
        }
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
        
        public Customer getCustomer() {
            return customer;
        }
        
        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
        
        public String getProcessorResponse() {
            return processorResponse;
        }
        
        public void setProcessorResponse(String processorResponse) {
            this.processorResponse = processorResponse;
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("name")
        private String name;
        
        @JsonProperty("email")
        private String email;
        
        @JsonProperty("phone_number")
        private String phoneNumber;
        
        public Customer() {}
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPhoneNumber() {
            return phoneNumber;
        }
        
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
