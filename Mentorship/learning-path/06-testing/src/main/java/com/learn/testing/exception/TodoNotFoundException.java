package com.learn.testing.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(Long id) { super("Todo not found: " + id); }
}

