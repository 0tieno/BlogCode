package com.learn.secured.service.impl;

import com.learn.secured.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: UserDetailsService
//
// This is a Spring Security interface with one job:
//   "Given an email, load the user from the database."
//
// Spring Security calls this from JwtAuthFilter when it needs to verify
// that the user in the token actually exists in the database.
//
// This lives in impl/ because it's the implementation of a Spring Security interface.
// ══════════════════════════════════════════════════════════════════════════════
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // Load from DB, then wrap in Spring Security's UserDetails object
        return userRepository.findByEmail(email)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}

