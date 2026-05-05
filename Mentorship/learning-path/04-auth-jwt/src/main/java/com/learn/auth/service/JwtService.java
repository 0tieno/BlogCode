package com.learn.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: JWT Service
//
// This service handles everything related to JWT tokens:
//   1. generateToken(email) → creates and signs a new token
//   2. extractEmail(token)  → reads the email from an existing token
//   3. isTokenValid(token)  → checks the token hasn't expired or been tampered with
//
// HOW SIGNING WORKS:
//   Server creates: header.payload.SIGNATURE
//   The SIGNATURE is computed using: header + payload + SECRET_KEY
//   If an attacker changes the payload, the signature won't match → rejected.
//   Only someone with the SECRET_KEY can create a valid token.
// ══════════════════════════════════════════════════════════════════════════════
@Service
public class JwtService {

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: @Value
    //
    // @Value("${property.name}") injects the value of that property
    // from application.properties into this field.
    //
    // WHY? Because:
    //   1. Secrets don't belong in source code (they'd be visible in git)
    //   2. Each environment (dev, production) can have a different secret
    //      without changing code — just change the environment variable
    // ══════════════════════════════════════════════════════════════════════════
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // SecretKey = a Key that's specifically for symmetric algorithms (HMAC)
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Creates a signed JWT token containing the user's email
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                                                  // WHO this token belongs to
                .issuedAt(new Date())                                            // WHEN it was issued
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // WHEN it expires
                .signWith(getSigningKey())                                       // SIGN with our secret
                .compact();                                                       // build the string
    }

    // Reads the email encoded inside the token (the "subject" claim)
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // Returns true if the token is valid (signature correct + not expired)
    public boolean isTokenValid(String token) {
        try {
            getClaims(token); // throws if invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // check signature
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

