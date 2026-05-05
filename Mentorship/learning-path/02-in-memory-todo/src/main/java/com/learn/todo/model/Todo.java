package com.learn.todo.model;

import java.time.LocalDateTime;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Internal Model vs DTO
//
// This is the INTERNAL representation of a Todo — what lives inside the service.
// It has fields the client should NOT control (id, createdAt).
//
// Compare this to:
//   TodoRequest  — what the client SENDS IN
//   TodoResponse — what we SEND BACK to the client
//
// This separation means the client can never fake an id or set a createdAt.
// ══════════════════════════════════════════════════════════════════════════════
public class Todo {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public Todo(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = false;           // always starts as not completed
        this.createdAt = LocalDateTime.now(); // always set by the server, not the client
    }

    // Getters and setters (in Project 3+ we'll use Lombok to remove this boilerplate)
    public Long getId()                            { return id; }
    public String getTitle()                       { return title; }
    public void setTitle(String title)             { this.title = title; }
    public String getDescription()                 { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted()                   { return completed; }
    public void setCompleted(boolean completed)    { this.completed = completed; }
    public LocalDateTime getCreatedAt()            { return createdAt; }
}

