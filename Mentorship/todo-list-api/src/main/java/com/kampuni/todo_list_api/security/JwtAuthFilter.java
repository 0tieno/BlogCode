package com.kampuni.todo_list_api.security;

import com.kampuni.todo_list_api.service.JwtService;
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

// ✅ This is why the security/ folder exists — JWT infrastructure lives here.

// ✅ OncePerRequestFilter guarantees this filter runs ONCE per HTTP request (not multiple times).
// ✅ This filter intercepts every incoming request and:
//    1. Reads the Authorization header
//    2. Extracts the JWT token
//    3. Validates the token
//    4. Tells Spring Security who is making the request
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Step 1: Read the Authorization header. A valid request looks like:
        //         Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
        String authHeader = request.getHeader("Authorization");

        // Step 2: If there's no token (e.g., /register or /login requests), let the request through.
        //         Spring Security will then enforce the .authorizeHttpRequests() rules.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Strip the "Bearer " prefix to get just the token string.
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        // Step 4: If we got an email AND the user is not already authenticated in this request:
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load the user from the database.
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Validate the token is not expired and the signature is correct.
            if (jwtService.isTokenValid(token)) {

                // ✅ Tell Spring Security: "This user is authenticated, with these roles."
                //    This is what allows the request to reach the controller.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,                          // credentials (null because we use JWT, not password here)
                        userDetails.getAuthorities()   // roles, e.g., ROLE_USER
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Store the authentication in Spring's SecurityContext for this request.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 5: Continue to the next filter / the controller.
        filterChain.doFilter(request, response);
    }
}



