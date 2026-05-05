package com.kampuni.todo_list_api.config;

import com.kampuni.todo_list_api.security.JwtAuthFilter;
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

// ✅ BEST PRACTICE: @EnableWebSecurity enables Spring Security's web security support.
@Configuration
@EnableWebSecurity
public class SecurityBeans {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ ADDED: This bean controls which endpoints are public and which require a logged-in user.
    //    Without this, Spring Security blocks EVERYTHING by default — including /register and /login!
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        return http
                // ✅ Disable CSRF — not needed for REST APIs that use JWT (no cookies/sessions).
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ STATELESS: Don't create HTTP sessions. JWTs make the server stateless.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ Define which endpoints are public vs. protected:
                .authorizeHttpRequests(auth -> auth
                        // Anyone can call register and login — they don't have a token yet!
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Everything else requires a valid JWT token.
                        .anyRequest().authenticated()
                )

                // ✅ Run our JWT filter BEFORE Spring checks username/password.
                //    This intercepts every request and extracts the user identity from the token.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }
}
