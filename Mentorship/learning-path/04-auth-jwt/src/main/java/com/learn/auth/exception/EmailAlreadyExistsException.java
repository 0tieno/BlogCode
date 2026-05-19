package com.learn.auth.exception;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Specific exception for "email already taken" during registration.
//
// HTTP 409 Conflict is the correct status for this case:
//   - 400 Bad Request = the data is malformed (wrong format)
//   - 409 Conflict    = the data is valid, but it conflicts with existing state
//
// A duplicate email is NOT a bad request — the format is fine.
// It conflicts with an EXISTING user. So 409 is correct.
// ══════════════════════════════════════════════════════════════════════════════
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}

