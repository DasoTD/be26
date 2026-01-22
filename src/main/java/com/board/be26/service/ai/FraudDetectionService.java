package com.board.be26.service.ai;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.board.be26.entity.Order;
import com.board.be26.repositories.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Service for AI-powered fraud detection using OpenAI REST API
 */
@Service
public class FraudDetectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionService.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    private final WebClient webClient;
    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    
    @Value("${OPENAI_API_KEY:sk-test-key}")
    private String openaiApiKey;
    
    @Value("${OPENAI_MODEL:gpt-3.5-turbo}")
    private String modelName;

    public FraudDetectionService(WebClient webClient, PaymentRepository paymentRepository, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.paymentRepository = paymentRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Analyze payment for fraud risk
     */
    public FraudAnalysisResult analyzeFraudRisk(Order order, String paymentMethod) {
        try {
            // Get user's payment history
            Long userId = order.getUser().getId();
            Long paymentCount = paymentRepository.countByOrder_User_Id(userId);
            BigDecimal totalSpent = paymentRepository.getTotalSpentByUserId(userId);
            
            // Build prompt for AI analysis
            String prompt = String.format(
                "Analyze the following payment for fraud risk and respond with ONLY a JSON object:\n" +
                "{\n" +
                "  \"riskLevel\": \"LOW|MEDIUM|HIGH\",\n" +
                "  \"riskScore\": 0-100,\n" +
                "  \"reasoning\": \"brief explanation\",\n" +
                "  \"requiresVerification\": true|false\n" +
                "}\n\n" +
                "Payment Details:\n" +
                "- Order Amount: $%s\n" +
                "- Payment Method: %s\n" +
                "- User Email: %s\n" +
                "- Product: %s (Qty: %d)\n" +
                "- User History: %d previous payments totaling $%s\n" +
                "- Currency: USD",
                order.getTotalPrice(),
                paymentMethod,
                order.getUser().getEmail(),
                order.getProduct().getName(),
                order.getQuantity(),
                paymentCount,
                totalSpent != null ? totalSpent : BigDecimal.ZERO
            );
            
            logger.info("Analyzing fraud risk for order {}", order.getId());
            
            // Call OpenAI API
            String response = callOpenAiApi(prompt);
            
            return parseFraudAnalysisResponse(response);
        } catch (Exception e) {
            logger.error("Error analyzing fraud risk for order {}", order.getId(), e);
            // Return medium risk on error to be safe
            return new FraudAnalysisResult("MEDIUM", 50, "AI analysis failed, defaulting to medium risk", true);
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
                Map.of("role", "system", "content", "You are a fraud detection analyst. Respond ONLY with valid JSON."),
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
     * Parse AI response into FraudAnalysisResult
     */
    private FraudAnalysisResult parseFraudAnalysisResponse(String response) {
        try {
            // Clean up response - remove markdown code blocks if present
            String cleanedResponse = response.trim()
                .replaceFirst("^```json\\s*", "")
                .replaceFirst("^```\\s*", "")
                .replaceFirst("\\s*```$", "");
            
            JsonNode jsonNode = objectMapper.readTree(cleanedResponse);
            
            String riskLevel = jsonNode.path("riskLevel").asText("MEDIUM");
            int riskScore = jsonNode.path("riskScore").asInt(50);
            String reasoning = jsonNode.path("reasoning").asText("Unable to determine risk");
            boolean requiresVerification = jsonNode.path("requiresVerification").asBoolean(true);
            
            return new FraudAnalysisResult(riskLevel, riskScore, reasoning, requiresVerification);
        } catch (Exception e) {
            logger.error("Error parsing fraud analysis response", e);
            return new FraudAnalysisResult("MEDIUM", 50, "Error parsing AI response", true);
        }
    }
    
    /**
     * Inner class for fraud analysis result
     */
    public static class FraudAnalysisResult {
        private String riskLevel;
        private int riskScore;
        private String reasoning;
        private boolean requiresVerification;
        
        public FraudAnalysisResult(String riskLevel, int riskScore, String reasoning, boolean requiresVerification) {
            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.reasoning = reasoning;
            this.requiresVerification = requiresVerification;
        }
        
        public String getRiskLevel() {
            return riskLevel;
        }
        
        public int getRiskScore() {
            return riskScore;
        }
        
        public String getReasoning() {
            return reasoning;
        }
        
        public boolean isRequiresVerification() {
            return requiresVerification;
        }
    }
}
