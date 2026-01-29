package com.board.be26.service.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.board.be26.entity.Order;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for AI-powered payment recommendations using OpenAI REST API
 */
@Service
public class PaymentRecommendationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentRecommendationService.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${OPENAI_API_KEY:sk-test-key}")
    private String openaiApiKey;
    
    @Value("${OPENAI_MODEL:gpt-3.5-turbo}")
    private String modelName;
    
    public PaymentRecommendationService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Get AI-powered payment recommendations
     */
    public PaymentRecommendation getPaymentRecommendation(Order order) {
        try {
            String prompt = String.format(
                "Analyze this purchase and provide payment recommendations. Respond with ONLY a JSON object:\n" +
                "{\n" +
                "  \"recommendedPaymentMethod\": \"paystack|flutterwave|both\",\n" +
                "  \"recommendedAmount\": \"full|partial\",\n" +
                "  \"suggestedInstallments\": 1-12,\n" +
                "  \"paymentTip\": \"brief advice\",\n" +
                "  \"savingsTip\": \"optional money-saving suggestion\"\n" +
                "}\n\n" +
                "Order Details:\n" +
                "- Total Amount: $%s\n" +
                "- Product: %s (Qty: %d)\n" +
                "- User Email: %s\n" +
                "- Currency: USD",
                order.getTotalPrice(),
                order.getProduct().getName(),
                order.getQuantity(),
                order.getUser().getEmail()
            );
            
            logger.info("Getting payment recommendation for order {}", order.getId());
            
            String response = callOpenAiApi(prompt);
            
            return parseRecommendationResponse(response);
        } catch (Exception e) {
            logger.error("Error getting payment recommendation for order {}", order.getId(), e);
            // Return default recommendation on error
            return new PaymentRecommendation(
                "paystack",
                "full",
                1,
                "Pay with your preferred payment method",
                "Consider bulk purchases for better savings"
            );
        }
    }
    
    /**
     * Call OpenAI API
     */
    private String callOpenAiApi(String prompt) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a payment advisor. Respond ONLY with valid JSON."),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 500);
            
            String response = webClient.post()
                    .uri(OPENAI_API_URL)
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            logger.debug("OpenAI API response: {}", response);
            
            if (response == null) {
                throw new RuntimeException("No response from OpenAI API");
            }
            
            // Extract content from response
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            return content;
            
        } catch (Exception e) {
            logger.error("Error calling OpenAI API", e);
            throw new RuntimeException("Failed to call OpenAI API: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parse AI response into PaymentRecommendation
     */
    private PaymentRecommendation parseRecommendationResponse(String response) {
        try {
            // Clean up response - remove markdown code blocks if present
            String cleanedResponse = response.trim()
                .replaceFirst("^```json\\s*", "")
                .replaceFirst("^```\\s*", "")
                .replaceFirst("\\s*```$", "");
            
            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);
            
            String recommendedMethod = jsonNode.path("recommendedPaymentMethod").asText("paystack");
            String recommendedAmount = jsonNode.path("recommendedAmount").asText("full");
            int suggestedInstallments = jsonNode.path("suggestedInstallments").asInt(1);
            String paymentTip = jsonNode.path("paymentTip").asText("Pay with your preferred payment method");
            String savingsTip = jsonNode.path("savingsTip").asText("");
            
            return new PaymentRecommendation(
                recommendedMethod,
                recommendedAmount,
                suggestedInstallments,
                paymentTip,
                savingsTip
            );
        } catch (Exception e) {
            logger.error("Error parsing recommendation response", e);
            return new PaymentRecommendation("paystack", "full", 1, "Standard payment", "");
        }
    }
    
    /**
     * Payment recommendation DTO
     */
    public static class PaymentRecommendation {
        private String recommendedPaymentMethod;
        private String recommendedAmount;
        private int suggestedInstallments;
        private String paymentTip;
        private String savingsTip;
        
        public PaymentRecommendation(String recommendedPaymentMethod, String recommendedAmount,
                                    int suggestedInstallments, String paymentTip, String savingsTip) {
            this.recommendedPaymentMethod = recommendedPaymentMethod;
            this.recommendedAmount = recommendedAmount;
            this.suggestedInstallments = suggestedInstallments;
            this.paymentTip = paymentTip;
            this.savingsTip = savingsTip;
        }
        
        public String getRecommendedPaymentMethod() { return recommendedPaymentMethod; }
        public String getRecommendedAmount() { return recommendedAmount; }
        public int getSuggestedInstallments() { return suggestedInstallments; }
        public String getPaymentTip() { return paymentTip; }
        public String getSavingsTip() { return savingsTip; }
    }
}
