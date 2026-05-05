package com.learn.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // unique = true adds a UNIQUE constraint — no two users can share an email
    @Column(unique = true, nullable = false)
    private String email;

    // ══════════════════════════════════════════════════════════════════════════
    // IMPORTANT: This field stores the HASHED password, not the plain text.
    // If someone reads your database, they see "$2a$10$abc..." not "password123".
    // ══════════════════════════════════════════════════════════════════════════
    @Column(nullable = false)
    private String password;

    public User() {}

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}

