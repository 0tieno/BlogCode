package com.learn.secured.config;

import com.learn.secured.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: SecurityFilterChain — the complete security policy
//
// This is the most important class in this project.
// It answers two questions:
//   1. WHICH endpoints are public?   (no token needed)
//   2. WHICH endpoints are protected? (token required)
//
// It also registers our JwtAuthFilter into the filter pipeline.
// ══════════════════════════════════════════════════════════════════════════════
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter)
            throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)        // REST API → no CSRF needed
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // no sessions
                .authorizeHttpRequests(auth -> auth
                        // These endpoints are PUBLIC — user doesn't have a token yet
                        .requestMatchers("/api/auth/**").permitAll()
                        // Everything else requires a valid JWT token
                        .anyRequest().authenticated()
                )
                // ══════════════════════════════════════════════════════════════
                // Register our custom JWT filter.
                // "BEFORE UsernamePasswordAuthenticationFilter" means it runs
                // early in the chain, before Spring tries its own auth mechanisms.
                // ══════════════════════════════════════════════════════════════
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

