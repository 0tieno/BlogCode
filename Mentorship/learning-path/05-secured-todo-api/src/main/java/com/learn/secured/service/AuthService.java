package com.learn.secured.service;

import com.learn.secured.dto.AuthResponse;
import com.learn.secured.dto.LoginRequest;
import com.learn.secured.dto.RegisterRequest;
import com.learn.secured.entity.User;
import com.learn.secured.exception.EmailAlreadyExistsException;
import com.learn.secured.exception.InvalidCredentialsException;
import com.learn.secured.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.email());
        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration failed — duplicate email: {}", request.email());
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password())); // ALWAYS hash passwords
        userRepository.save(user);
        log.info("User registered: {}", request.email());
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt: {}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login failed — email not found: {}", request.email());
                    return new InvalidCredentialsException(); // same as wrong password — no user enumeration
                });
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            log.warn("Login failed — wrong password: {}", request.email());
            throw new InvalidCredentialsException();
        }
        log.info("Login successful: {}", request.email());
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }
}
