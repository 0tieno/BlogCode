package com.learn.secured.exception;

// HTTP 409 Conflict — email is valid but already taken by another user.
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}

