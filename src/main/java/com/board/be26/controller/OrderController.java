package com.board.be26.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.be26.dto.CreateOrderRequest;
import com.board.be26.entity.Order;
import com.board.be26.entity.User;
import com.board.be26.repositories.UserRepository;
import com.board.be26.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request, Principal principal) {
        requireOwnership(principal, request.getUserId());
        Order created = orderService.createOrder(request);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders(Principal principal) {
        // Basic safeguard: only authenticated users can view their own orders. For full admin support, add roles later.
        User user = requirePrincipalUser(principal);
        return ResponseEntity.ok(orderService.getOrdersForUser(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id, Principal principal) {
        Order order = orderService.getOrder(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        requireOwnership(principal, order.getUser().getId());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersForUser(@PathVariable Long userId, Principal principal) {
        requireOwnership(principal, userId);
        return ResponseEntity.ok(orderService.getOrdersForUser(userId));
    }

    private void requireOwnership(Principal principal, Long userId) {
        User user = requirePrincipalUser(principal);
        if (!user.getId().equals(userId)) {
            throw new SecurityException("Forbidden: cannot access orders for another user");
        }
    }

    private User requirePrincipalUser(Principal principal) {
        if (principal == null) {
            throw new SecurityException("Authentication required");
        }
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new SecurityException("Authenticated user not found"));
    }
}
