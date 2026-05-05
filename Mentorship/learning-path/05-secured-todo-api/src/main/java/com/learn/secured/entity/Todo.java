package com.learn.secured.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity @Table(name = "todos") @Getter @Setter
public class Todo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String title;
    private String description;
    @Column(nullable = false) private boolean completed = false;
    @Column(nullable = false, updatable = false) private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    public Todo() {}
    @PrePersist public void prePersist() { this.createdAt = LocalDateTime.now(); }
}

