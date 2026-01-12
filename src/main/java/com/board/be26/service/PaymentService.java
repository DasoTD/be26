package com.board.be26.service;

import java.math.RoundingMode;

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

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
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
}
