package com.board.be26.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;  // Hashed
    private String email;
    private BigDecimal balance = BigDecimal.ZERO;
    private String stripeCustomerId;  // Optional for Stripe
    private String stripeBankAccountId;  // For payouts
}

// public class User {
//     private Long id;
//     private String username;
//     private String email;

//     // Constructors, getters, and setters

//     public User() {
//     }

//     public User(Long id, String username, String email) {
//         this.id = id;
//         this.username = username;
//         this.email = email;
//     }

//     public Long getId() {
//         return id;
//     }

//     public void setId(Long id) {
//         this.id = id;
//     }

//     public String getUsername() {
//         return username;
//     }

//     public void setUsername(String username) {
//         this.username = username;
//     }

//     public String getEmail() {
//         return email;
//     }

//     public void setEmail(String email) {
//         this.email = email;
//     }
// }
