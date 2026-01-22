package com.board.be26.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlutterwaveInitializeResponse {
    
    private boolean status;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private FlutterwaveData data;
    
    public FlutterwaveInitializeResponse() {}
    
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
    
    public FlutterwaveData getData() {
        return data;
    }
    
    public void setData(FlutterwaveData data) {
        this.data = data;
    }
    
    /**
     * Nested class representing Flutterwave transaction data
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlutterwaveData {
        @JsonProperty("id")
        private Long id;
        
        @JsonProperty("tx_ref")
        private String txRef;
        
        @JsonProperty("flw_ref")
        private String flwRef;
        
        @JsonProperty("device_fingerprint")
        private String deviceFingerprint;
        
        @JsonProperty("amount")
        private Double amount;
        
        @JsonProperty("currency")
        private String currency;
        
        @JsonProperty("customer")
        private Customer customer;
        
        @JsonProperty("status")
        private String status;
        
        @JsonProperty("auth_url")
        private String authUrl;
        
        @JsonProperty("access_code")
        private String accessCode;
        
        @JsonProperty("link")
        private String link;
        
        public FlutterwaveData() {}
        
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
        
        public String getDeviceFingerprint() {
            return deviceFingerprint;
        }
        
        public void setDeviceFingerprint(String deviceFingerprint) {
            this.deviceFingerprint = deviceFingerprint;
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
        
        public Customer getCustomer() {
            return customer;
        }
        
        public void setCustomer(Customer customer) {
            this.customer = customer;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public String getAuthUrl() {
            return authUrl;
        }
        
        public void setAuthUrl(String authUrl) {
            this.authUrl = authUrl;
        }
        
        public String getAccessCode() {
            return accessCode;
        }
        
        public void setAccessCode(String accessCode) {
            this.accessCode = accessCode;
        }
        
        public String getLink() {
            return link;
        }
        
        public void setLink(String link) {
            this.link = link;
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
