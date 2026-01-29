package com.board.be26.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.board.be26.dto.CreateOrderRequest;
import com.board.be26.entity.Order;
import com.board.be26.entity.OrderStatus;
import com.board.be26.entity.Product;
import com.board.be26.entity.User;
import com.board.be26.event.OrderCreatedEvent;
import com.board.be26.repositories.OrderRepository;
import com.board.be26.repositories.ProductRepository;
import com.board.be26.repositories.UserRepository;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository, 
                       ProductRepository productRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one product must be specified");
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalQuantity = 0;
        Product firstProduct = null;

        // Validate all products and calculate total
        for (var item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));

            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero");
            }
            if (product.getStock() < item.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }

            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalQuantity += item.getQuantity();
            
            if (firstProduct == null) {
                firstProduct = product;
            }
        }

        // Deduct stock for all products
        for (var item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        // Create order (using first product as primary, with total quantity)
        Order order = new Order();
        order.setUser(user);
        order.setProduct(firstProduct);
        order.setQuantity(totalQuantity);
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);

        Order saved = orderRepository.save(order);
        logger.info("Created order {} for user {} with {} items, total price: {}", 
            saved.getId(), user.getId(), request.getItems().size(), totalPrice);
        
        // Publish order created event to RabbitMQ queue
        OrderCreatedEvent event = new OrderCreatedEvent(
            saved.getId(),
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            firstProduct.getName() + (request.getItems().size() > 1 ? " + " + (request.getItems().size() - 1) + " more" : ""),
            totalQuantity,
            firstProduct.getPrice(),
            totalPrice,
            saved.getStatus().toString(),
            System.currentTimeMillis()
        );
        eventPublisher.publishOrderCreatedEvent(event);
        
        return saved;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public List<Order> getOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
