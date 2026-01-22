package com.board.be26.service.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.board.be26.service.FlutterwaveService;
import com.board.be26.service.PaystackService;

@Component
public class PaymentProviderFactory {
    private static final Logger logger = LoggerFactory.getLogger(PaymentProviderFactory.class);
    
    @Value("${payment.default-provider:paystack}")
    private String defaultProvider;
    
    private final PaystackService paystackService;
    private final FlutterwaveService flutterwaveService;
    
    public PaymentProviderFactory(PaystackService paystackService, FlutterwaveService flutterwaveService) {
        this.paystackService = paystackService;
        this.flutterwaveService = flutterwaveService;
    }
    
    /**
     * Get the payment provider based on configuration
     * @return The configured PaymentProvider instance
     */
    public PaymentProvider getProvider() {
        logger.info("Loading payment provider: {}", defaultProvider);
        
        return switch (defaultProvider.toLowerCase()) {
            case "flutterwave" -> flutterwaveService;
            case "paystack" -> paystackService;
            default -> {
                logger.warn("Unknown payment provider: {}, defaulting to paystack", defaultProvider);
                yield paystackService;
            }
        };
    }
    
    /**
     * Get a specific payment provider by name
     * @param providerName The name of the provider (paystack, flutterwave)
     * @return The PaymentProvider instance, or null if not found
     */
    public PaymentProvider getProvider(String providerName) {
        logger.info("Loading payment provider: {}", providerName);
        
        return switch (providerName.toLowerCase()) {
            case "flutterwave" -> flutterwaveService;
            case "paystack" -> paystackService;
            default -> {
                logger.warn("Unknown payment provider: {}", providerName);
                yield null;
            }
        };
    }
}
