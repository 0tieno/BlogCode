package com.learn.auth.service;

import com.learn.auth.dto.AuthResponse;
import com.learn.auth.dto.LoginRequest;
import com.learn.auth.dto.RegisterRequest;
import com.learn.auth.entity.User;
import com.learn.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Authentication Service
//
// Two operations:
//   register() → create a new user, hash the password, return a token
//   login()    → find the user, verify the password, return a token
// ══════════════════════════════════════════════════════════════════════════════
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // BCryptPasswordEncoder (defined in SecurityConfig)
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        // Check: is this email already taken?
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());

        // ══════════════════════════════════════════════════════════════════
        // CRITICAL: NEVER do: user.setPassword(request.password())
        //   That would store "password123" in the database — catastrophic!
        //
        // ALWAYS hash first:
        //   passwordEncoder.encode("password123") → "$2a$10$N9qo..."
        //   This is a one-way operation. You can NEVER get "password123" back from the hash.
        // ══════════════════════════════════════════════════════════════════
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        // Step 1: Does this user exist?
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // ══════════════════════════════════════════════════════════════════
        // Step 2: Does the password match?
        //
        // passwordEncoder.matches(rawPassword, hashedPassword)
        //   It hashes 'rawPassword' and compares to 'hashedPassword'.
        //   Returns true if they match.
        //
        // SECURITY NOTE: We throw "Invalid credentials" for BOTH wrong email
        //   AND wrong password. If we said "Wrong password", an attacker would
        //   know the email exists. "Invalid credentials" gives no hints.
        //   This prevents a "user enumeration" attack.
        // ══════════════════════════════════════════════════════════════════
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Step 3: Credentials are correct → generate and return a token
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }
}

