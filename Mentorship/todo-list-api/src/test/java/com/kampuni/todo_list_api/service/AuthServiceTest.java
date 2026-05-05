package com.kampuni.todo_list_api.service;

import com.kampuni.todo_list_api.dto.AuthResponse;
import com.kampuni.todo_list_api.dto.LoginRequest;
import com.kampuni.todo_list_api.dto.RegisterRequest;
import com.kampuni.todo_list_api.entity.User;
import com.kampuni.todo_list_api.repository.UserRepository;
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

// ✅ CONCEPT: Unit Tests vs Integration Tests
//    - Unit test: tests ONE class in isolation. All dependencies are FAKED (mocked).
//    - Integration test: tests multiple real classes working together (needs a database, etc.)
//
//    This is a unit test. We mock UserRepository, PasswordEncoder, and JwtService
//    so that AuthService is tested ALONE without any real database or JWT library.

// ✅ @ExtendWith(MockitoExtension.class) activates Mockito for this test class.
//    It processes the @Mock and @InjectMocks annotations automatically.
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // ✅ @Mock creates a fake version of this class.
    //    It does nothing by default — you tell it what to return with when().thenReturn().
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    // ✅ @InjectMocks creates a real AuthService and injects the @Mock fields into its constructor.
    @InjectMocks
    private AuthService authService;

    // ─────────────────────────────────────────────────────────────────────────
    // register()
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void register_newEmail_savesUserAndReturnsToken() {
        // ARRANGE — set up the fake behaviours
        RegisterRequest request = new RegisterRequest("Alice", "alice@test.com", "password123");

        // Simulate: no user with this email exists yet
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        // Simulate: BCrypt returns a hashed password
        when(passwordEncoder.encode("password123")).thenReturn("$2a$hashed");
        // Simulate: JWT library returns a token
        when(jwtService.generateToken("alice@test.com")).thenReturn("fake-jwt-token");
        // Simulate: save() returns the user back (JPA convention)
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        AuthResponse response = authService.register(request);

        // ASSERT
        assertThat(response.token()).isEqualTo("fake-jwt-token");

        // ✅ verify() checks that a method was actually called with the right argument.
        //    Here we confirm the password was hashed (never stored as plain text).
        verify(passwordEncoder).encode("password123");
        // Confirm the user was actually saved to the "database".
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateEmail_throwsRuntimeException() {
        // ARRANGE — simulate an existing user with the same email
        RegisterRequest request = new RegisterRequest("Alice", "alice@test.com", "password123");
        when(userRepository.findByEmail("alice@test.com"))
                .thenReturn(Optional.of(new User("Alice", "alice@test.com", "hashed")));

        // ACT & ASSERT — expect an exception with the right message
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already exists");

        // ✅ Confirm that save() was NEVER called — no half-saved user in the DB.
        verify(userRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // login()
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void login_correctCredentials_returnsToken() {
        // ARRANGE
        LoginRequest request = new LoginRequest("alice@test.com", "password123");
        User storedUser = new User("Alice", "alice@test.com", "$2a$hashed");

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(storedUser));
        // passwordEncoder.matches(rawPassword, hashedPassword) → true means correct password
        when(passwordEncoder.matches("password123", "$2a$hashed")).thenReturn(true);
        when(jwtService.generateToken("alice@test.com")).thenReturn("fake-jwt-token");

        // ACT
        AuthResponse response = authService.login(request);

        // ASSERT
        assertThat(response.token()).isEqualTo("fake-jwt-token");
    }

    @Test
    void login_emailNotFound_throwsInvalidCredentials() {
        // ARRANGE — no user with this email in the "database"
        LoginRequest request = new LoginRequest("ghost@test.com", "password123");
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                // ✅ SECURITY CHECK: Both wrong-email and wrong-password give the SAME message
                //    so attackers can't tell which one failed (user enumeration prevention).
                .hasMessage("Invalid credentials");
    }

    @Test
    void login_wrongPassword_throwsInvalidCredentials() {
        // ARRANGE — user exists, but the password is wrong
        LoginRequest request = new LoginRequest("alice@test.com", "wrongpassword");
        User storedUser = new User("Alice", "alice@test.com", "$2a$hashed");

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches("wrongpassword", "$2a$hashed")).thenReturn(false);

        // ACT & ASSERT
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");

        // ✅ Confirm no token was ever generated for a failed login
        verify(jwtService, never()).generateToken(anyString());
    }
}

