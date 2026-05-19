package com.learn.testing.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() { super("Invalid credentials"); }
}

