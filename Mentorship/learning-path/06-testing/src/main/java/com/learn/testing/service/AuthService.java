package com.learn.testing.service;

import com.learn.testing.dto.*;
import com.learn.testing.entity.User;
import com.learn.testing.exception.EmailAlreadyExistsException;
import com.learn.testing.exception.InvalidCredentialsException;
import com.learn.testing.repository.UserRepository;
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

    public AuthService(UserRepository r, PasswordEncoder p, JwtService j) {
        userRepository = r;
        passwordEncoder = p;
        jwtService = j;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new EmailAlreadyExistsException(req.email());
        }
        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        userRepository.save(user);
        log.info("User registered: {}", req.email());
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        log.info("Login successful: {}", req.email());
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }
}
