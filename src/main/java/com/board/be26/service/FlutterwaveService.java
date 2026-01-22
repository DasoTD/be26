package com.board.be26.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.board.be26.dto.FlutterwaveInitializeResponse;
import com.board.be26.dto.FlutterwaveVerifyResponse;
import com.board.be26.service.payment.PaymentProvider;

@Service
public class FlutterwaveService implements PaymentProvider {
    private static final Logger logger = LoggerFactory.getLogger(FlutterwaveService.class);
    
    @Value("${flutterwave.secret-key}")
    private String secretKey;
    
    private final WebClient webClient;
    
    public FlutterwaveService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.flutterwave.com/v3").build();
    }
    
    /**
     * Initialize a Flutterwave transaction
     */
    @Override
    public Object initializeTransaction(String email, BigDecimal amount, String reference) {
        try {
            long amountInKobo = amount.multiply(BigDecimal.valueOf(100)).longValue();
            
            String requestBody = String.format(
                "{\"email\":\"%s\",\"amount\":%d,\"tx_ref\":\"%s\",\"redirect_url\":\"http://localhost:3000/verify\"}",
                email, amountInKobo, reference
            );
            
            logger.info("Initializing Flutterwave transaction for email: {}, amount: {}", email, amount);
            
            FlutterwaveInitializeResponse response = webClient.post()
                    .uri("/payments")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(FlutterwaveInitializeResponse.class)
                    .block();
            
            if (response != null && response.isStatus()) {
                logger.info("Flutterwave transaction initialized successfully: {}", reference);
            } else {
                logger.error("Failed to initialize Flutterwave transaction: {}", reference);
            }
            
            return response;
        } catch (Exception e) {
            logger.error("Error initializing Flutterwave transaction", e);
            throw new RuntimeException("Failed to initialize payment", e);
        }
    }
    
    /**
     * Verify a Flutterwave transaction
     */
    @Override
    public Object verifyTransaction(String reference) {
        try {
            logger.info("Verifying Flutterwave transaction: {}", reference);
            
            FlutterwaveVerifyResponse response = webClient.get()
                    .uri("/transactions/verify_by_reference?tx_ref={reference}", reference)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey)
                    .retrieve()
                    .bodyToMono(FlutterwaveVerifyResponse.class)
                    .block();
            
            if (response != null && response.isStatus()) {
                logger.info("Flutterwave transaction verified: {} - Status: {}", 
                           reference, response.getData().getStatus());
            } else {
                logger.error("Failed to verify Flutterwave transaction: {}", reference);
            }
            
            return response;
        } catch (Exception e) {
            logger.error("Error verifying Flutterwave transaction", e);
            throw new RuntimeException("Failed to verify payment", e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "flutterwave";
    }
}
