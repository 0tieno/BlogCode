package com.kampuni.todo_list_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// ✅ BEST PRACTICE: Entity class names are usually singular nouns. ToDo → Todo is fine.
//    The table name "todos" is correct (plural, lowercase, snake_case).
@Entity
@Table(name = "todos")
@Getter
@Setter
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ BEST PRACTICE: Add @Column constraints so the DB schema is explicit.
    @Column(nullable = false)
    private String title;

    private String description;

    // ✅ ADDED: A todo without a "completed" field is incomplete!
    @Column(nullable = false)
    private boolean completed = false;

    // ✅ BEST PRACTICE: Always track when a record was created.
    //    updatable = false means this value is set ONCE on insert and never changed.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ✅ BEST PRACTICE: Many todos belong to one user.
    //    fetch = LAZY is important for performance — don't load the user unless you need it.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ToDo() {
    }

    // ✅ BEST PRACTICE: Use @PrePersist to automatically set createdAt before saving.
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
