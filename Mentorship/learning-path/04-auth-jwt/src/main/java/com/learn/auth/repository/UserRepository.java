package com.learn.auth.repository;

import com.learn.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring generates: SELECT * FROM users WHERE email = ?
    // Returns Optional because the user might not exist
    Optional<User> findByEmail(String email);
}

