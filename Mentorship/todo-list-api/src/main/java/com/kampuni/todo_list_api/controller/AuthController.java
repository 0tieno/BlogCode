package com.kampuni.todo_list_api.controller;

import com.kampuni.todo_list_api.dto.AuthResponse;
import com.kampuni.todo_list_api.dto.LoginRequest;
import com.kampuni.todo_list_api.dto.RegisterRequest;
import com.kampuni.todo_list_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// ✅ BEST PRACTICE: Group all auth endpoints under /api/v1/auth
//    Versioning the API (v1) lets you release a v2 later without breaking existing clients.
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    // ✅ GOOD: Constructor injection — the Spring recommended approach.
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/v1/auth/register
    // ✅ FIXED: Return type is ResponseEntity<AuthResponse>, not ResponseEntity<AuthService>
    // ✅ BEST PRACTICE: Return 201 CREATED for resource creation, not 200 OK.
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(registerRequest));
    }

    // ✅ ADDED: POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
