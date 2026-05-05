package com.kampuni.todo_list_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// ✅ FIXED: Removed unused @Email import — @Email belongs on DTOs (input validation), not entities.
// ✅ BEST PRACTICE: @Data from Lombok gives you @Getter + @Setter + @EqualsAndHashCode + @ToString.
//    But for JPA entities, avoid @Data because it can cause infinite loops in @ToString
//    when you have bidirectional relationships. @Getter + @Setter is the safer choice.
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

    @Column(unique = true, nullable = false)
    private String email;

    // ✅ NOTE: This stores the HASHED password (BCrypt). Never store plain text passwords.
    @Column(nullable = false)
    private String password;

    // ✅ BEST PRACTICE: Always have a no-arg constructor for JPA.
    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
