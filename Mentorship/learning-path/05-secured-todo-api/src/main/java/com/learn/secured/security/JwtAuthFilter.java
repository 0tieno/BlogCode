package com.learn.secured.security;

import com.learn.secured.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: OncePerRequestFilter — "run this code for every HTTP request"
//
// Spring Security uses a "filter chain" — a series of filters that every request
// passes through before reaching the controller.
//
// This filter does 5 things:
//   1. Read the Authorization header
//   2. Extract the JWT token
//   3. Validate the token (not expired, not tampered)
//   4. Load the user from the database
//   5. Put the user into SecurityContextHolder
//      → this tells Spring Security "this request is authenticated as this user"
// ══════════════════════════════════════════════════════════════════════════════
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Read header. Format:  Authorization: Bearer eyJhbGci...
        String authHeader = request.getHeader("Authorization");

        // Step 2: If no token (e.g., /register or /login), skip filtering and continue.
        //   Spring Security will apply the permitAll() rule from SecurityConfig.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Strip "Bearer " prefix
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        // Step 4: Only authenticate if we got an email AND the request isn't already authenticated
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(token)) {
                // Step 5: Create an authentication object and store it in the SecurityContext
                //   From this moment on, Spring Security considers this request authenticated.
                //   In services: SecurityContextHolder.getContext().getAuthentication().getName()
                //   will return this user's email.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response); // continue to the next filter / controller
    }
}

