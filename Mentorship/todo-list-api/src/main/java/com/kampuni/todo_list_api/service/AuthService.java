package com.kampuni.todo_list_api.service;

import com.kampuni.todo_list_api.dto.AuthResponse;
import com.kampuni.todo_list_api.dto.LoginRequest;
import com.kampuni.todo_list_api.dto.RegisterRequest;
import com.kampuni.todo_list_api.entity.User;
import com.kampuni.todo_list_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // ✅ FIXED: Return type is AuthResponse (the DTO), not AuthService (the class itself).
    //    NEVER return a service class from a service method — that makes no sense.
    public AuthResponse register(RegisterRequest registerRequest) {

        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            // ✅ NOTE: We will handle this with a custom exception later (see GlobalExceptionHandler).
            //    Throwing RuntimeException directly is okay for now but not ideal for production.
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(registerRequest.name());
        user.setEmail(registerRequest.email());
        // ✅ GOOD: You are already hashing the password with BCrypt. Never store plain text!
        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        userRepository.save(user);

        // ✅ FIXED: new AuthResponse(token) — wraps the token in the response DTO.
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    // ✅ ADDED: Login method. Without this, users can register but never log in again!
    public AuthResponse login(LoginRequest loginRequest) {

        // Step 1: Find the user by email. orElseThrow gives a clear error if not found.
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Step 2: Check if the provided password matches the stored hashed password.
        // ✅ SECURITY: Always use passwordEncoder.matches() — NEVER compare plain text to hash directly.
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            // ✅ SECURITY: Use the same error message for wrong email AND wrong password.
            //    Otherwise attackers can tell which one is wrong (user enumeration attack).
            throw new RuntimeException("Invalid credentials");
        }

        // Step 3: Generate and return a JWT token.
        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
