package com.learn.testing.service;
import com.learn.testing.dto.*;
import com.learn.testing.entity.User;
import com.learn.testing.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(UserRepository r, PasswordEncoder p, JwtService j) { userRepository=r; passwordEncoder=p; jwtService=j; }
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) throw new RuntimeException("Email already exists");
        User user = new User(); user.setName(req.name()); user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email()).orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) throw new RuntimeException("Invalid credentials");
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }
}

