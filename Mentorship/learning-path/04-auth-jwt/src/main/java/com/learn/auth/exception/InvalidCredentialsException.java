package com.learn.auth.exception;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Specific exception for failed login attempts.
//
// HTTP 401 Unauthorized is the correct status for this:
//   - 401 = "I don't know who you are" (authentication failed)
//   - 403 = "I know who you are, but you can't do this" (authorization failed)
//
// When login credentials are wrong, authentication fails → 401.
//
// SECURITY NOTE: The message "Invalid credentials" is deliberately vague.
//   It does NOT say "wrong email" or "wrong password".
//   If we said "wrong password", an attacker would know the email EXISTS,
//   giving them half the information they need. This is a "user enumeration"
//   attack. The same generic message for both wrong email and wrong password
//   prevents this.
// ══════════════════════════════════════════════════════════════════════════════
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}

