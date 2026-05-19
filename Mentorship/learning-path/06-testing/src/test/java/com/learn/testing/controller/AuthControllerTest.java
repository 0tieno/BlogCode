package com.learn.testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.testing.dto.AuthResponse;
import com.learn.testing.dto.LoginRequest;
import com.learn.testing.dto.RegisterRequest;
import com.learn.testing.exception.EmailAlreadyExistsException;
import com.learn.testing.exception.GlobalExceptionHandler;
import com.learn.testing.exception.InvalidCredentialsException;
import com.learn.testing.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Controller Integration Tests using standaloneSetup
//
// In Spring Boot 4, @WebMvcTest (a "test slice") was removed.
// The recommended approach is MockMvcBuilders.standaloneSetup(), which:
//
//   ✅ Tests the REAL controller code (routing, @Valid, @RequestBody, status codes)
//   ✅ Tests the REAL GlobalExceptionHandler (exception → HTTP status mapping)
//   ✅ Uses Mockito @Mock for the service (same as unit tests — no Spring context)
//   ✅ Extremely fast — no Spring Boot startup, no database, no security filters
//
// The difference from pure unit tests:
//   Unit test (AuthServiceTest):    calls authService.register() directly
//   Controller test (this file):    sends HTTP POST /api/auth/register via MockMvc
//                                   and verifies the HTTP status code and JSON body
//
// WHY BOTH?
//   Unit tests catch logic bugs. Controller tests catch HTTP contract bugs:
//   "Did the controller return 201 or 200? Is the JSON field named 'token'?
//    Does @Valid reject an empty name with 400? Does a duplicate email return 409?"
// ══════════════════════════════════════════════════════════════════════════════
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    // ── Same @Mock pattern as unit tests — no @MockBean needed ──────────────
    @Mock
    private AuthService authService;

    // The REAL controller, Mockito injects the fake service into it
    @InjectMocks
    private AuthController authController;

    // MockMvc simulates HTTP requests without a real server
    private MockMvc mockMvc;

    // Converts Java objects → JSON strings for request bodies
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // ══════════════════════════════════════════════════════════════════════
        // CONCEPT: standaloneSetup
        //
        // standaloneSetup(controller) builds a minimal MockMvc that:
        //   - Routes requests to the given controller
        //   - Runs @Valid validation
        //   - Processes @RequestBody / @ResponseBody JSON conversion
        //   - Does NOT load any Spring beans (no DB, no security)
        //
        // setControllerAdvice(...) registers our GlobalExceptionHandler so that
        // exception → HTTP status mappings work exactly as in production.
        // ══════════════════════════════════════════════════════════════════════
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler()) // wire in exception → status mapping
                .build();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // register() — happy path → 201 Created
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void register_validRequest_returns201WithToken() throws Exception {
        when(authService.register(any())).thenReturn(new AuthResponse("fake-jwt-token"));

        RegisterRequest body = new RegisterRequest("Alice", "alice@test.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())                        // 201 Created (not 200!)
                .andExpect(jsonPath("$.token").value("fake-jwt-token")); // JSON: { "token": "..." }
        //              ↑ jsonPath uses dot notation to navigate the JSON response body
    }

    // ──────────────────────────────────────────────────────────────────────────
    // register() — empty name → @Valid rejects it → 400 Bad Request
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void register_emptyName_returns400WithFieldError() throws Exception {
        // ══════════════════════════════════════════════════════════════════════
        // CONCEPT: Testing @Valid at the HTTP layer
        //
        // In unit tests (AuthServiceTest), we bypass validation and call the
        // service directly. Here we go through the controller, so @Valid RUNS.
        //
        // Flow: MockMvc → AuthController → @Valid fails → GlobalExceptionHandler
        //       → 400 Bad Request with { "name": "error message" }
        //
        // The service is NEVER called when validation fails.
        // ══════════════════════════════════════════════════════════════════════
        RegisterRequest body = new RegisterRequest("", "alice@test.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())      // 400
                .andExpect(jsonPath("$.name").exists()); // field-level error on "name"
    }

    // ──────────────────────────────────────────────────────────────────────────
    // register() — password too short (min 8 chars) → 400
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void register_shortPassword_returns400WithFieldError() throws Exception {
        RegisterRequest body = new RegisterRequest("Alice", "alice@test.com", "short");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").exists()); // field-level error on "password"
    }

    // ──────────────────────────────────────────────────────────────────────────
    // register() — duplicate email → EmailAlreadyExistsException → 409 Conflict
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void register_duplicateEmail_returns409() throws Exception {
        // Service throws EmailAlreadyExistsException
        // GlobalExceptionHandler catches it and returns 409 Conflict
        when(authService.register(any())).thenThrow(new EmailAlreadyExistsException("alice@test.com"));

        RegisterRequest body = new RegisterRequest("Alice", "alice@test.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict())          // 409 Conflict ← not 400 Bad Request!
                .andExpect(jsonPath("$.error").exists());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // login() — valid credentials → 200 OK with token
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        when(authService.login(any())).thenReturn(new AuthResponse("fake-jwt-token"));

        LoginRequest body = new LoginRequest("alice@test.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())   // login returns 200 OK (not 201, no resource created)
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // login() — wrong credentials → InvalidCredentialsException → 401
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void login_wrongCredentials_returns401() throws Exception {
        when(authService.login(any())).thenThrow(new InvalidCredentialsException());

        LoginRequest body = new LoginRequest("alice@test.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())                    // 401 Unauthorized ← not 400!
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }
}
