package com.board.be26.service;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.board.be26.config.RabbitMQConfig;
import com.board.be26.event.OrderCreatedEvent;

@Service
public class OrderEmailConsumer {
    private static final Logger logger = LoggerFactory.getLogger(OrderEmailConsumer.class);
    
    private final JavaMailSender mailSender;
    
    public OrderEmailConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @RabbitListener(queues = RabbitMQConfig.ORDER_EMAIL_QUEUE)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            logger.info("Received order created event for order {} from queue", event.getOrderId());
            
            String subject = "Order Confirmation - Order #" + event.getOrderId();
            String message = buildOrderConfirmationEmail(event);
            
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(event.getUserEmail());
            email.setSubject(subject);
            email.setText(message);
            
            mailSender.send(email);
            logger.info("Order confirmation email sent successfully to {} for order {}", 
                       event.getUserEmail(), event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to send order confirmation email for order {}", event.getOrderId(), e);
            throw new RuntimeException("Failed to send email for order " + event.getOrderId(), e);
        }
    }
    
    private String buildOrderConfirmationEmail(OrderCreatedEvent event) {
        return "Dear " + event.getUsername() + ",\n\n" +
               "Thank you for your order!\n\n" +
               "Order Details:\n" +
               "Order ID: " + event.getOrderId() + "\n" +
               "Product: " + event.getProductName() + "\n" +
               "Quantity: " + event.getQuantity() + "\n" +
               "Unit Price: $" + event.getUnitPrice() + "\n" +
               "Total Price: $" + event.getTotalPrice() + "\n" +
               "Status: " + event.getOrderStatus() + "\n" +
               "Order Date: " + new java.util.Date(event.getCreatedAt()) + "\n\n" +
               "Your order is being processed. We will notify you when it's shipped.\n\n" +
               "Thank you for shopping with us!\n\n" +
               "Best regards,\n" +
               "The Store Team";
    }
}
