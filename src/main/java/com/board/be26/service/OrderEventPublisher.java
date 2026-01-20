package com.board.be26.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.board.be26.config.RabbitMQConfig;
import com.board.be26.event.OrderCreatedEvent;

@Service
public class OrderEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventPublisher.class);
    
    private final RabbitTemplate rabbitTemplate;
    
    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        try {
            logger.info("Publishing order created event for order {}", event.getOrderId());
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EMAIL_EXCHANGE,
                RabbitMQConfig.ORDER_EMAIL_ROUTING_KEY,
                event
            );
            logger.info("Order created event published successfully for order {}", event.getOrderId());
        } catch (Exception e) {
            logger.error("Failed to publish order created event for order {}", event.getOrderId(), e);
        }
    }
}
