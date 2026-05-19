package com.learn.auth.service;

import com.learn.auth.dto.AuthResponse;
import com.learn.auth.dto.LoginRequest;
import com.learn.auth.dto.RegisterRequest;
import com.learn.auth.entity.User;
import com.learn.auth.exception.EmailAlreadyExistsException;
import com.learn.auth.exception.InvalidCredentialsException;
import com.learn.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: @Slf4j — Logging
//
// Lombok's @Slf4j generates a private static 'log' field.
// This is equivalent to writing:
//   private static final Logger log = LoggerFactory.getLogger(AuthService.class);
//
// WHY LOG?
//   Without logs, when something goes wrong in production you're blind.
//   Logs let you trace exactly what happened, when, and with what data.
//
// LOG LEVELS (from least to most severe):
//   log.debug() — detailed debugging info (only shown in dev with DEBUG enabled)
//   log.info()  — normal application events (user registered, request received)
//   log.warn()  — something unexpected but recoverable (failed login attempt)
//   log.error() — something broke (unexpected exception, DB error)
//
// RULE: Never log sensitive data (passwords, tokens, credit card numbers).
// ══════════════════════════════════════════════════════════════════════════════
@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: @Transactional
    //
    // @Transactional wraps this method in a database transaction. That means:
    //   - If the method succeeds → all DB changes are COMMITTED (made permanent).
    //   - If the method throws → all DB changes are ROLLED BACK (undone).
    //
    // WHY THIS MATTERS:
    //   Imagine register() does two things: save user, then send a welcome email.
    //   If the email send fails AFTER the user is saved, without @Transactional
    //   the user would remain in the database in a half-created state.
    //   With @Transactional, the user save is also rolled back → consistent state.
    //
    // Spring handles all of this automatically. You just add the annotation.
    //
    // READ operations don't modify data, so they don't need @Transactional.
    // WRITE operations (INSERT, UPDATE, DELETE) always should have it.
    // ══════════════════════════════════════════════════════════════════════════
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.email());

        // ── Custom exception → 409 Conflict (not 400 Bad Request) ───────────
        // Before: throw new RuntimeException("Email already exists")
        // After:  throw new EmailAlreadyExistsException(email) → GlobalExceptionHandler → 409
        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration failed — email already exists: {}", request.email());
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password())); // ALWAYS hash — never store plain text
        userRepository.save(user);

        log.info("User registered successfully: {}", request.email());
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        // ── Custom exception → 401 Unauthorized ─────────────────────────────
        // SECURITY: same exception class for "email not found" AND "wrong password"
        // → same HTTP status (401) AND same response body ("Invalid credentials")
        // → an attacker cannot tell whether the email exists or not
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed — email not found: {}", request.email());
                    return new InvalidCredentialsException();
                });

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed — wrong password for: {}", request.email());
            throw new InvalidCredentialsException();
        }

        log.info("Login successful for: {}", request.email());
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }
}
