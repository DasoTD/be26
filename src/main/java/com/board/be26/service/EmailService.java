package com.board.be26.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.board.be26.entity.Order;
import com.board.be26.entity.Product;
import com.board.be26.entity.User;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmation(Order order) {
        try {
            User user = order.getUser();
            Product product = order.getProduct();
            
            String subject = "Order Confirmation - Order #" + order.getId();
            String message = buildOrderConfirmationEmail(order, user, product);
            
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(user.getEmail());
            email.setSubject(subject);
            email.setText(message);
            
            mailSender.send(email);
            logger.info("Order confirmation email sent to {} for order {}", user.getEmail(), order.getId());
        } catch (Exception e) {
            logger.error("Failed to send order confirmation email for order {}", order.getId(), e);
            // Don't throw exception - email failure shouldn't block order creation
        }
    }

    private String buildOrderConfirmationEmail(Order order, User user, Product product) {
        BigDecimal totalPrice = order.getTotalPrice();
        
        return "Dear " + user.getUsername() + ",\n\n" +
               "Thank you for your order!\n\n" +
               "Order Details:\n" +
               "Order ID: " + order.getId() + "\n" +
               "Product: " + product.getName() + "\n" +
               "Quantity: " + order.getQuantity() + "\n" +
               "Unit Price: $" + product.getPrice() + "\n" +
               "Total Price: $" + totalPrice + "\n" +
               "Status: " + order.getStatus() + "\n" +
               "Order Date: " + order.getCreatedAt() + "\n\n" +
               "Your order is being processed. We will notify you when it's shipped.\n\n" +
               "Thank you for shopping with us!\n\n" +
               "Best regards,\n" +
               "The Store Team";
    }
}
