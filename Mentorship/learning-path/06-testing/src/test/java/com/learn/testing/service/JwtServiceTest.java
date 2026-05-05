package com.learn.testing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Testing without Spring (pure unit test)
//
// There is NO @SpringBootTest here. No Spring context, no database, no HTTP.
// We just create a JwtService object directly and test its methods.
// This is the fastest kind of test — runs in milliseconds.
//
// The only challenge: JwtService has @Value fields (secret, expirationMs).
// @Value only works when Spring is running. In a pure test, we inject values
// manually using ReflectionTestUtils.setField().
// ══════════════════════════════════════════════════════════════════════════════
class JwtServiceTest {

    private JwtService jwtService;

    // A valid secret — must be at least 32 characters for HMAC-SHA256
    private static final String SECRET = "test-secret-key-that-is-long-enough-for-hmac256-yep!";

    @BeforeEach  // runs before each @Test method
    void setUp() {
        jwtService = new JwtService();
        // Inject the private @Value fields manually
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3_600_000L); // 1 hour
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Test: generate a token, then extract the email back out
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void generateToken_thenExtractEmail_returnsOriginalEmail() {
        // ARRANGE
        String email = "alice@test.com";

        // ACT
        String token = jwtService.generateToken(email);
        String extracted = jwtService.extractEmail(token);

        // ASSERT
        // assertThat() is from AssertJ — more readable than assertEquals("alice", email)
        assertThat(extracted).isEqualTo(email);
    }

    @Test
    void generateToken_differentEmails_produceDifferentTokens() {
        // The token encodes the email in it — different emails = different tokens
        String tokenAlice = jwtService.generateToken("alice@test.com");
        String tokenBob   = jwtService.generateToken("bob@test.com");

        assertThat(tokenAlice).isNotEqualTo(tokenBob);
        assertThat(jwtService.extractEmail(tokenAlice)).isEqualTo("alice@test.com");
        assertThat(jwtService.extractEmail(tokenBob)).isEqualTo("bob@test.com");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Test: valid tokens return true from isTokenValid()
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void isTokenValid_freshToken_returnsTrue() {
        String token = jwtService.generateToken("alice@test.com");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Test: a tampered token fails validation
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void isTokenValid_tamperedPayload_returnsFalse() {
        // A JWT has 3 parts: header.payload.signature
        // If we change the payload, the signature no longer matches → invalid
        String token = jwtService.generateToken("alice@test.com");
        String[] parts = token.split("\\.");
        String fakePayload = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"hacker@evil.com\"}".getBytes());
        String tampered = parts[0] + "." + fakePayload + "." + parts[2];

        assertThat(jwtService.isTokenValid(tampered)).isFalse();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Test: an expired token fails validation
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        // Override expiration to -1ms → already expired the moment it's created
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1L);
        String expiredToken = jwtService.generateToken("alice@test.com");

        assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Test: extractEmail on tampered token throws an exception
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void extractEmail_tamperedToken_throwsException() {
        String token = jwtService.generateToken("alice@test.com");
        String[] parts = token.split("\\.");
        String fakePayload = java.util.Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"sub\":\"hacker@evil.com\"}".getBytes());
        String tampered = parts[0] + "." + fakePayload + "." + parts[2];

        // assertThatThrownBy: verifies that calling this lambda THROWS an exception
        assertThatThrownBy(() -> jwtService.extractEmail(tampered))
                .isInstanceOf(Exception.class);
    }
}

