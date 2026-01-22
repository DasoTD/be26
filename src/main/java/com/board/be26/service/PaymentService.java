package com.board.be26.service;

import java.math.RoundingMode;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.be26.dto.PaymentRequest;
import com.board.be26.dto.PaymentResponse;
import com.board.be26.entity.Order;
import com.board.be26.entity.OrderStatus;
import com.board.be26.entity.Payment;
import com.board.be26.entity.PaymentStatus;
import com.board.be26.entity.User;
import com.board.be26.repositories.OrderRepository;
import com.board.be26.repositories.PaymentRepository;
import com.board.be26.repositories.UserRepository;
import com.board.be26.dto.PaystackInitializeResponse;
import com.board.be26.dto.PaystackVerifyResponse;
import com.board.be26.service.payment.PaymentProvider;
import com.board.be26.service.payment.PaymentProviderFactory;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentProviderFactory providerFactory;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, 
                         UserRepository userRepository, PaymentProviderFactory providerFactory) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.providerFactory = providerFactory;
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SecurityException("Authenticated user not found"));
        Order order = orderRepository.findById(request.getOrderId()).orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Forbidden: cannot pay for another user's order");
        }
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Order already paid");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is cancelled");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalPrice().setScale(2, RoundingMode.HALF_UP));
        payment.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setProviderReference(request.getProviderReference());
        payment.setStatus(PaymentStatus.SUCCEEDED); // Demo: assume success; integrate gateway for real payments.

        Payment saved = paymentRepository.save(payment);
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long id, String username) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null) {
            return null;
        }
        if (!payment.getOrder().getUser().getUsername().equals(username)) {
            throw new SecurityException("Forbidden");
        }
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse resp = new PaymentResponse();
        resp.setId(payment.getId());
        resp.setOrderId(payment.getOrder().getId());
        resp.setAmount(payment.getAmount());
        resp.setCurrency(payment.getCurrency());
        resp.setStatus(payment.getStatus());
        resp.setPaymentMethod(payment.getPaymentMethod());
        resp.setProviderReference(payment.getProviderReference());
        resp.setCreatedAt(payment.getCreatedAt());
        resp.setUpdatedAt(payment.getUpdatedAt());
        return resp;
    }
    
    // ========== PAYSTACK INTEGRATION ==========
    
    /**
     * Initialize payment for an order via the configured provider
     */
    @Transactional
    public PaystackInitializeResponse initializePaymentPaystack(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            
            String reference = "ORD-" + orderId + "-" + UUID.randomUUID().toString().substring(0, 8);
            
            PaymentProvider provider = providerFactory.getProvider();
            Object response = provider.initializeTransaction(
                order.getUser().getEmail(),
                order.getTotalPrice(),
                reference
            );
            
            // Cast to appropriate response type based on provider
            PaystackInitializeResponse initResponse = null;
            if (response instanceof com.board.be26.dto.FlutterwaveInitializeResponse) {
                // Flutterwave response - extract relevant data
                com.board.be26.dto.FlutterwaveInitializeResponse flwResponse = 
                    (com.board.be26.dto.FlutterwaveInitializeResponse) response;
                if (flwResponse.isStatus()) {
                    Payment payment = new Payment();
                    payment.setOrder(order);
                    payment.setAmount(order.getTotalPrice());
                    payment.setProviderReference(reference);
                    payment.setStatus(PaymentStatus.INITIATED);
                    payment.setPaymentMethod("flutterwave");
                    paymentRepository.save(payment);
                    
                    logger.info("Payment initialized via Flutterwave for order {}: {}", orderId, reference);
                }
            } else if (response instanceof PaystackInitializeResponse) {
                // Paystack response
                initResponse = (PaystackInitializeResponse) response;
                if (initResponse != null && initResponse.isStatus()) {
                    Payment payment = new Payment();
                    payment.setOrder(order);
                    payment.setAmount(order.getTotalPrice());
                    payment.setProviderReference(reference);
                    payment.setStatus(PaymentStatus.INITIATED);
                    payment.setPaymentMethod(provider.getProviderName());
                    paymentRepository.save(payment);
                    
                    logger.info("Payment initialized for order {}: {}", orderId, reference);
                }
            }
            
            return initResponse;
        } catch (Exception e) {
            logger.error("Error initializing payment for order {}", orderId, e);
            throw new RuntimeException("Failed to initialize payment", e);
        }
    }
    
    /**
     * Verify payment and update order status via the configured provider
     */
    @Transactional
    public boolean verifyPaymentPaystack(String reference) {
        try {
            Payment payment = paymentRepository.findByProviderReference(reference)
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + reference));
            
            PaymentProvider provider = providerFactory.getProvider();
            Object response = provider.verifyTransaction(reference);
            
            if (response instanceof com.board.be26.dto.FlutterwaveVerifyResponse) {
                // Flutterwave verification response
                com.board.be26.dto.FlutterwaveVerifyResponse flwResponse = 
                    (com.board.be26.dto.FlutterwaveVerifyResponse) response;
                
                if (flwResponse != null && flwResponse.isStatus()) {
                    String status = flwResponse.getData().getStatus();
                    
                    if ("successful".equalsIgnoreCase(status)) {
                        payment.setStatus(PaymentStatus.SUCCEEDED);
                        paymentRepository.save(payment);
                        
                        Order order = payment.getOrder();
                        order.setStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                        
                        logger.info("Payment verified and completed via Flutterwave for order {}", order.getId());
                        return true;
                    } else if ("failed".equalsIgnoreCase(status)) {
                        payment.setStatus(PaymentStatus.FAILED);
                        paymentRepository.save(payment);
                        logger.warn("Payment failed via Flutterwave for reference {}", reference);
                        return false;
                    }
                }
            } else if (response instanceof PaystackVerifyResponse) {
                // Paystack verification response
                PaystackVerifyResponse verifyResponse = (PaystackVerifyResponse) response;
                
                if (verifyResponse != null && verifyResponse.isStatus()) {
                    String status = verifyResponse.getData().getStatus();
                    
                    if ("success".equalsIgnoreCase(status)) {
                        payment.setStatus(PaymentStatus.SUCCEEDED);
                        paymentRepository.save(payment);
                        
                        Order order = payment.getOrder();
                        order.setStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                        
                        logger.info("Payment verified and completed for order {}", order.getId());
                        return true;
                    } else if ("abandoned".equalsIgnoreCase(status) || "failed".equalsIgnoreCase(status)) {
                        payment.setStatus(PaymentStatus.FAILED);
                        paymentRepository.save(payment);
                        logger.warn("Payment failed for reference {}", reference);
                        return false;
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error verifying payment for reference {}", reference, e);
            throw new RuntimeException("Failed to verify payment", e);
        }
    }
    
    /**
     * Get payment by reference
     */
    public Payment getPaymentByReference(String reference) {
        return paymentRepository.findByProviderReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + reference));
    }
}
