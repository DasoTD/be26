package com.board.be26.dto;

public class PaystackInitializeResponse {
    private boolean status;
    private String message;
    private PaystackData data;

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

    public PaystackData getData() {
        return data;
    }

    public void setData(PaystackData data) {
        this.data = data;
    }

    public static class PaystackData {
        private Long authorization_url;
        private String access_code;
        private String reference;

        public Long getAuthorization_url() {
            return authorization_url;
        }

        public void setAuthorization_url(Long authorization_url) {
            this.authorization_url = authorization_url;
        }

        public String getAccess_code() {
            return access_code;
        }

        public void setAccess_code(String access_code) {
            this.access_code = access_code;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }
    }
}
