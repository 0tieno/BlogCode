package com.learn.testing.service;

import com.learn.testing.dto.AuthResponse;
import com.learn.testing.dto.LoginRequest;
import com.learn.testing.dto.RegisterRequest;
import com.learn.testing.entity.User;
import com.learn.testing.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Mockito — faking dependencies
//
// AuthService depends on: UserRepository, PasswordEncoder, JwtService.
// In a unit test we don't want real versions of those — we want FAKES we control.
//
// @ExtendWith(MockitoExtension.class) activates Mockito annotations.
// @Mock         creates a fake object.
// @InjectMocks  creates the REAL object and injects the fakes into it.
//
// Result: we test AuthService's logic WITHOUT hitting a database or real JWT library.
// ══════════════════════════════════════════════════════════════════════════════
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;   // fake — does nothing until told
    @Mock PasswordEncoder passwordEncoder; // fake
    @Mock JwtService jwtService;           // fake

    // Real AuthService, but its constructor receives the fakes above
    @InjectMocks AuthService authService;

    // ──────────────────────────────────────────────────────────────────────────
    // register() — happy path
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void register_newEmail_savesUserAndReturnsToken() {
        // ARRANGE: tell the fakes what to return
        RegisterRequest req = new RegisterRequest("Alice", "alice@test.com", "password123");

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty()); // no existing user
        when(passwordEncoder.encode("password123")).thenReturn("$2a$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0)); // return what was passed in
        when(jwtService.generateToken("alice@test.com")).thenReturn("fake-token");

        // ACT
        AuthResponse response = authService.register(req);

        // ASSERT
        assertThat(response.token()).isEqualTo("fake-token");

        // verify() checks the method was actually called
        verify(passwordEncoder).encode("password123"); // must hash the password
        verify(userRepository).save(any(User.class));  // must save to "database"
    }

    // ──────────────────────────────────────────────────────────────────────────
    // register() — duplicate email
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void register_duplicateEmail_throwsException() {
        RegisterRequest req = new RegisterRequest("Alice", "alice@test.com", "password123");
        when(userRepository.findByEmail("alice@test.com"))
                .thenReturn(Optional.of(new User("Alice", "alice@test.com", "hashed")));

        // assertThatThrownBy = this code MUST throw the specified exception
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already exists");

        // never() = verify this was NOT called (no partial saves on failure)
        verify(userRepository, never()).save(any());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // login() — correct credentials
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void login_correctCredentials_returnsToken() {
        LoginRequest req = new LoginRequest("alice@test.com", "password123");
        User storedUser = new User("Alice", "alice@test.com", "$2a$hashed");

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("password123", "$2a$hashed")).thenReturn(true); // password matches
        when(jwtService.generateToken("alice@test.com")).thenReturn("fake-token");

        AuthResponse response = authService.login(req);

        assertThat(response.token()).isEqualTo("fake-token");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // login() — wrong email
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void login_emailNotFound_throwsInvalidCredentials() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("ghost@test.com", "pass")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // login() — wrong password
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void login_wrongPassword_throwsInvalidCredentialsAndNeverGeneratesToken() {
        LoginRequest req = new LoginRequest("alice@test.com", "wrongpassword");
        when(userRepository.findByEmail("alice@test.com"))
                .thenReturn(Optional.of(new User("Alice", "alice@test.com", "$2a$hashed")));
        when(passwordEncoder.matches("wrongpassword", "$2a$hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");

        // Token must NEVER be generated for a failed login
        verify(jwtService, never()).generateToken(anyString());
    }
}

