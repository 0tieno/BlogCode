package com.kampuni.todo_list_api.service.impl;

import com.kampuni.todo_list_api.entity.User;
import com.kampuni.todo_list_api.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

// ✅ BEST PRACTICE: This is why the impl/ folder exists!
//    The pattern is: define an interface (UserDetailsService from Spring Security),
//    then put your concrete implementation in impl/.
//    This makes it easy to swap implementations (e.g., load users from LDAP instead of DB)
//    without changing any other code.

// ✅ UserDetailsService is a Spring Security interface.
//    Spring Security calls loadUserByUsername() to look up the user when validating a JWT.
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@org.jspecify.annotations.NonNull String email) throws UsernameNotFoundException {
        // Load the user from the database by their email address.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert our User entity into Spring Security's UserDetails object.
        // "ROLE_USER" is a basic role — you can add more roles later (ROLE_ADMIN, etc.)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}


