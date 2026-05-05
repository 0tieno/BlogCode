package com.learn.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Spring Security Configuration
//
// When you add spring-boot-starter-security to pom.xml, Spring AUTOMATICALLY
// locks down ALL endpoints. You can't access ANYTHING without a password.
//
// This class overrides that default behaviour.
// We tell Spring: "here's how I want security to work".
// ══════════════════════════════════════════════════════════════════════════════
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: PasswordEncoder Bean
    //
    // A @Bean method tells Spring: "create this object and manage it for me".
    // Spring creates ONE instance and provides it wherever PasswordEncoder is needed.
    //
    // BCryptPasswordEncoder is the industry standard for hashing passwords.
    // The number 10 is the "work factor" — how many times to hash.
    // Higher = slower to compute = harder to brute-force. 10-12 is standard.
    // ══════════════════════════════════════════════════════════════════════════
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: SecurityFilterChain
    //
    // This is the main security configuration. Spring Security works as a chain
    // of filters — each request passes through them before reaching your controller.
    //
    // In this project (04), we make ALL endpoints public so you can test
    // register and login easily. In Project 05, we protect the todo endpoints.
    // ══════════════════════════════════════════════════════════════════════════
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF (Cross-Site Request Forgery protection).
                // CSRF matters for browsers that use cookies. REST APIs use tokens → not needed.
                .csrf(AbstractHttpConfigurer::disable)

                // STATELESS: don't create HTTP sessions.
                // With JWT, the server doesn't need to remember clients between requests.
                // The token carries everything needed.
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Allow all requests in this demo project.
                // (In Project 05, we'll restrict this properly)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                .build();
    }
}

