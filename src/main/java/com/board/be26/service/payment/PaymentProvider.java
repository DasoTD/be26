package com.board.be26.service.payment;

import java.math.BigDecimal;

/**
 * Interface for payment provider integrations (Paystack, Flutterwave, etc.)
 * Each provider returns its own DTO types
 */
public interface PaymentProvider {
    
    /**
     * Initialize a payment transaction
     * @return Provider-specific response (PaystackInitializeResponse or FlutterwaveInitializeResponse)
     */
    Object initializeTransaction(String email, BigDecimal amount, String reference);
    
    /**
     * Verify a payment transaction
     * @return Provider-specific response (PaystackVerifyResponse or FlutterwaveVerifyResponse)
     */
    Object verifyTransaction(String reference);
    
    /**
     * Get provider name
     */
    String getProviderName();
}
