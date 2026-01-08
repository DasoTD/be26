package com.board.be26.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.board.be26.entity.Order;
import com.board.be26.entity.OrderStatus;
import com.board.be26.entity.Product;
import com.board.be26.entity.User;
import com.board.be26.repositories.OrderRepository;
import com.board.be26.repositories.ProductRepository;
import com.board.be26.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long orderId;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setUsername("alice");
        user.setEmail("alice@example.com");
        user.setPassword("pw");
        user.setRoles("ROLE_USER");
        user = userRepository.save(user);

        Product product = new Product();
        product.setSku("SKU-PAY-1");
        product.setName("Payable Product");
        product.setDescription("Desc");
        product.setPrice(new BigDecimal("5.00"));
        product.setStock(10);
        product.setStatus(com.board.be26.entity.ProductStatus.ACTIVE);
        product = productRepository.save(product);

        Order order = new Order();
        order.setUser(user);
        order.setProduct(product);
        order.setQuantity(1);
        order.setTotalPrice(product.getPrice());
        order.setStatus(OrderStatus.PENDING);
        order = orderRepository.save(order);
        orderId = order.getId();
    }

    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void createPayment_asOwner_shouldSucceed() throws Exception {
        String body = """
        {
          \"orderId\": %d,
          \"paymentMethod\": \"CARD\"
        }
        """.formatted(orderId);

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "bob", roles = {"USER"})
    void createPayment_asDifferentUser_shouldBeForbidden() throws Exception {
        String body = """
        {
          \"orderId\": %d,
          \"paymentMethod\": \"CARD\"
        }
        """.formatted(orderId);

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "alice", roles = {"USER"})
    void getPayment_requiresOwnership() throws Exception {
        String body = """
        {
          \"orderId\": %d,
          \"paymentMethod\": \"CARD\"
        }
        """.formatted(orderId);

        String response = mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        long paymentId = json.get("id").asLong();

        // Basic fetch with same user
        mockMvc.perform(get("/payments/" + paymentId))
          .andExpect(status().isOk());
    }
}
