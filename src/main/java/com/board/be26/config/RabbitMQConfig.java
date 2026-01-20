package com.board.be26.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    
    // Queue names
    public static final String ORDER_EMAIL_QUEUE = "order.email.queue";
    public static final String ORDER_EMAIL_EXCHANGE = "order.email.exchange";
    public static final String ORDER_EMAIL_ROUTING_KEY = "order.email.routing.key";
    
    // Queue Declaration
    @Bean
    public Queue orderEmailQueue() {
        return new Queue(ORDER_EMAIL_QUEUE, true, false, false);
    }
    
    // Exchange Declaration
    @Bean
    public DirectExchange orderEmailExchange() {
        return new DirectExchange(ORDER_EMAIL_EXCHANGE, true, false);
    }
    
    // Binding
    @Bean
    public Binding orderEmailBinding(Queue orderEmailQueue, DirectExchange orderEmailExchange) {
        return BindingBuilder.bind(orderEmailQueue)
                .to(orderEmailExchange)
                .with(ORDER_EMAIL_ROUTING_KEY);
    }
    
    // Message Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
