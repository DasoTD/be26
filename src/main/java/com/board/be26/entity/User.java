package com.board.be26.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "uk_users_username", columnList = "username", unique = true),
    @Index(name = "uk_users_email", columnList = "email", unique = true)
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;  // Hashed
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String roles = "ROLE_USER";
    private BigDecimal balance = BigDecimal.ZERO;
    private String stripeCustomerId;  // Optional for Stripe
    private String stripeBankAccountId;  // For payouts

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public String getStripeBankAccountId() {
        return stripeBankAccountId;
    }

    public void setStripeBankAccountId(String stripeBankAccountId) {
        this.stripeBankAccountId = stripeBankAccountId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
