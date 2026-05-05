package com.kampuni.todo_list_api.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    // ✅ FIXED: Secret is no longer hardcoded in the source code.
    //    @Value reads it from application.properties. This way, each environment
    //    (dev, staging, production) can have its own secret without changing code.
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    // ✅ BEST PRACTICE: Returns SecretKey (not just Key) — required by jjwt 0.12+ API.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ✅ FIXED: jjwt 0.12+ uses .subject() instead of deprecated .setSubject()
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ✅ FIXED: jjwt 0.12+ uses Jwts.parser().verifyWith() instead of deprecated parserBuilder()
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ ADDED: A utility method to check if a token is still valid (not expired, not tampered).
    public boolean isTokenValid(String token) {
        try {
            getClaims(token); // will throw if expired or signature is wrong
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
