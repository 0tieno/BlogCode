package com.kampuni.todo_list_api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// ✅ No annotations needed here — JwtService has no Spring dependencies.
//    We construct it directly with "new JwtService()" and inject values manually.
//
// ✅ What is ReflectionTestUtils?
//    JwtService uses @Value to read `jwt.secret` from application.properties.
//    In a unit test there is no Spring context, so @Value won't inject anything.
//    ReflectionTestUtils.setField() lets us set private fields directly in tests.
class JwtServiceTest {

    private JwtService jwtService;

    // ✅ PATTERN: A secret that is exactly 64 characters = 512 bits — well above the 256-bit minimum.
    private static final String TEST_SECRET =
            "test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm!";
    private static final long ONE_HOUR_MS = 3_600_000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Manually inject the @Value fields since there's no Spring context in unit tests.
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", ONE_HOUR_MS);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // generateToken + extractEmail
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void generateToken_thenExtractEmail_returnsOriginalEmail() {
        // ARRANGE — the email we want to put inside the token
        String email = "alice@test.com";

        // ACT — generate the token, then read it back
        String token = jwtService.generateToken(email);
        String extracted = jwtService.extractEmail(token);

        // ASSERT — the email we put in must be the email we get out
        assertThat(extracted).isEqualTo(email);
    }

    @Test
    void generateToken_differentEmails_produceDifferentTokens() {
        // ✅ NOTE: Two tokens for the SAME email generated in the same second will be identical
        //    because JWT uses second-level precision for issuedAt. That's expected behavior.
        //    What we DO care about is that different subjects produce different tokens.
        String tokenAlice = jwtService.generateToken("alice@test.com");
        String tokenBob   = jwtService.generateToken("bob@test.com");

        assertThat(tokenAlice).isNotEqualTo(tokenBob);
        // And that each token encodes the correct owner
        assertThat(jwtService.extractEmail(tokenAlice)).isEqualTo("alice@test.com");
        assertThat(jwtService.extractEmail(tokenBob)).isEqualTo("bob@test.com");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // isTokenValid
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void isTokenValid_freshToken_returnsTrue() {
        String token = jwtService.generateToken("alice@test.com");

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_tamperedToken_returnsFalse() {
        // ✅ CONCEPT: A JWT has 3 parts: header.payload.signature
        //    If you change ANY character the signature check fails.
        String validToken = jwtService.generateToken("alice@test.com");
        String tamperedToken = validToken + "tampered";

        assertThat(jwtService.isTokenValid(tamperedToken)).isFalse();
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        // Override expiration to -1ms so the token is already expired the moment it's created.
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1L);

        String expiredToken = jwtService.generateToken("alice@test.com");

        assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
    }

    @Test
    void extractEmail_tamperedToken_throwsException() {
        // ✅ A JWT has 3 base64-encoded parts: header.payload.signature
        //    We swap out the PAYLOAD section entirely — this makes the signature invalid.
        String validToken = jwtService.generateToken("alice@test.com");
        String[] parts = validToken.split("\\.");

        // Replace the payload (parts[1]) with a fake one
        String fakePayload = java.util.Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString("{\"sub\":\"hacker@evil.com\"}".getBytes());
        String tamperedToken = parts[0] + "." + fakePayload + "." + parts[2];

        assertThatThrownBy(() -> jwtService.extractEmail(tamperedToken))
                .isInstanceOf(Exception.class);
    }
}



