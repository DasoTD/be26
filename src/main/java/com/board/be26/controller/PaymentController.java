package com.board.be26.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.board.be26.dto.PaymentRequest;
import com.board.be26.dto.PaymentResponse;
import com.board.be26.entity.Payment;
import com.board.be26.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request, Principal principal) {
        PaymentResponse response = paymentService.createPayment(request, principal.getName());
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id, Principal principal) {
        PaymentResponse response = paymentService.getPayment(id, principal.getName());
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
    
    // ========== PAYMENT PROVIDER INTEGRATION (Provider-Agnostic) ==========
    
    /**
     * Initialize payment for an order (works with any configured provider)
     */
    @PostMapping("/initialize/{orderId}")
    public ResponseEntity<Object> initializePayment(
            @PathVariable Long orderId, 
            Principal principal) {
        Object response = paymentService.initializePaymentPaystack(orderId);
        return ResponseEntity.status(201).body(response);
    }
    
    /**
     * Verify payment (works with any configured provider)
     */
    @PostMapping("/verify")
    public ResponseEntity<Object> verifyPayment(@RequestParam String reference) {
        try {
            boolean verified = paymentService.verifyPaymentPaystack(reference);
            if (verified) {
                return ResponseEntity.ok(new PaymentStatusResponse(true, "Payment verified and completed"));
            } else {
                return ResponseEntity.ok(new PaymentStatusResponse(false, "Payment verification failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new PaymentStatusResponse(false, e.getMessage()));
        }
    }
    
    /**
     * Get payment by reference
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<Payment> getPaymentByReference(@PathVariable String reference) {
        Payment payment = paymentService.getPaymentByReference(reference);
        return ResponseEntity.ok(payment);
    }
    
    // Legacy endpoints for backward compatibility
    /**
     * @deprecated Use POST /payments/initialize/{orderId} instead
     */
    @PostMapping("/paystack/initialize/{orderId}")
    @Deprecated(forRemoval = true)
    public ResponseEntity<Object> initializePaystackPayment(
            @PathVariable Long orderId, 
            Principal principal) {
        Object response = paymentService.initializePaymentPaystack(orderId);
        return ResponseEntity.status(201).body(response);
    }
    
    /**
     * @deprecated Use POST /payments/verify instead
     */
    @PostMapping("/paystack/verify")
    @Deprecated(forRemoval = true)
    public ResponseEntity<Object> verifyPaystackPayment(@RequestParam String reference) {
        try {
            boolean verified = paymentService.verifyPaymentPaystack(reference);
            if (verified) {
                return ResponseEntity.ok(new PaymentStatusResponse(true, "Payment verified and completed"));
            } else {
                return ResponseEntity.ok(new PaymentStatusResponse(false, "Payment verification failed"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new PaymentStatusResponse(false, e.getMessage()));
        }
    }
    
    // Response wrapper class
    public static class PaymentStatusResponse {
        private boolean success;
        private String message;
        
        public PaymentStatusResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
