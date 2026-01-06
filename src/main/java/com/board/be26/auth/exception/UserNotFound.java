package com.board.be26.auth.exception;

public class UserNotFound extends RuntimeException {
    public UserNotFound(String message) {
        super(message);
    }
    
}

// extends RuntimeException {
    
//     public AccountNotFoundException(Long accountId) {
//         super("Account not found with ID: " + accountId);
//     }