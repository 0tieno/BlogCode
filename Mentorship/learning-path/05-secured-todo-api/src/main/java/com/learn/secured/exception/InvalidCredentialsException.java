package com.learn.secured.exception;

// HTTP 401 Unauthorized — login failed (wrong email or wrong password).
// Message is deliberately vague to prevent user enumeration attacks.
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}

