package com.board.be26.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.board.be26.dto.PaystackInitializeResponse;
import com.board.be26.dto.PaystackVerifyResponse;

@Service
public class PaystackService {
    private static final Logger logger = LoggerFactory.getLogger(PaystackService.class);
    
    @Value("${paystack.secret-key}")
    private String secretKey;
    
    private final WebClient webClient;
    
    public PaystackService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.paystack.co").build();
    }
    
    /**
     * Initialize a Paystack transaction
     */
    public PaystackInitializeResponse initializeTransaction(String email, BigDecimal amount, 
                                                            String reference) {
        try {
            String amountInKobo = amount.multiply(BigDecimal.valueOf(100)).toPlainString();
            
            String requestBody = String.format(
                "{\"email\":\"%s\",\"amount\":%s,\"reference\":\"%s\"}",
                email, amountInKobo, reference
            );
            
            logger.info("Initializing Paystack transaction for email: {}, amount: {}", email, amount);
            
            PaystackInitializeResponse response = webClient.post()
                    .uri("/transaction/initialize")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(PaystackInitializeResponse.class)
                    .block();
            
            if (response != null && response.isStatus()) {
                logger.info("Paystack transaction initialized successfully: {}", reference);
            } else {
                logger.error("Failed to initialize Paystack transaction: {}", reference);
            }
            
            return response;
        } catch (Exception e) {
            logger.error("Error initializing Paystack transaction", e);
            throw new RuntimeException("Failed to initialize payment", e);
        }
    }
    
    /**
     * Verify a Paystack transaction
     */
    public PaystackVerifyResponse verifyTransaction(String reference) {
        try {
            logger.info("Verifying Paystack transaction: {}", reference);
            
            PaystackVerifyResponse response = webClient.get()
                    .uri("/transaction/verify/{reference}", reference)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                    .retrieve()
                    .bodyToMono(PaystackVerifyResponse.class)
                    .block();
            
            if (response != null && response.isStatus()) {
                logger.info("Paystack transaction verified: {} - Status: {}", 
                           reference, response.getData().getStatus());
            } else {
                logger.error("Failed to verify Paystack transaction: {}", reference);
            }
            
            return response;
        } catch (Exception e) {
            logger.error("Error verifying Paystack transaction", e);
            throw new RuntimeException("Failed to verify payment", e);
        }
    }
}
