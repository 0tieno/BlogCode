package com.learn.dbtodo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: @Entity — mapping a Java class to a database table
//
// By adding @Entity, you're telling JPA:
//   "Create a database table for this class.
//    Each field becomes a column. Each instance becomes a row."
//
// JPA/Hibernate reads these annotations and:
//   - Creates the table schema (because of ddl-auto=update in application.properties)
//   - Generates the SQL INSERT, UPDATE, SELECT, DELETE for you
// ══════════════════════════════════════════════════════════════════════════════
@Entity

// @Table lets you control the table name. Without it, JPA uses the class name ("Todo").
// Convention: table names are snake_case and plural.
@Table(name = "todos")

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Lombok
//
// Without Lombok, you'd write 40+ lines of getters, setters, and constructors.
// With @Getter and @Setter, Lombok generates them at compile time.
// You write the field; Lombok writes getId(), setId(), getTitle(), setTitle(), etc.
//
// NOTE: We use @Getter + @Setter separately instead of @Data because
//       @Data can cause problems with JPA relationships (infinite loops in toString).
// ══════════════════════════════════════════════════════════════════════════════
@Getter
@Setter
public class Todo {

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: Primary Key
    //
    // @Id         = this field is the primary key (unique identifier for each row)
    // @GeneratedValue = the database auto-generates the value
    //   IDENTITY strategy = uses database AUTO_INCREMENT (1, 2, 3, ...)
    //   You NEVER set this yourself — the database does it.
    // ══════════════════════════════════════════════════════════════════════════
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: @Column
    //
    // Controls how the field maps to a database column.
    // nullable = false → adds NOT NULL constraint to the database column.
    // Without @Column, the field maps to a nullable column with the same name.
    // ══════════════════════════════════════════════════════════════════════════
    @Column(nullable = false)
    private String title;

    private String description; // nullable by default — that's fine for description

    @Column(nullable = false)
    private boolean completed = false;

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: updatable = false
    //
    // This column is set ONCE when the row is inserted (INSERT).
    // If you call UPDATE later, this column is NOT updated — it keeps its original value.
    // Perfect for "created at" timestamps.
    // ══════════════════════════════════════════════════════════════════════════
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // JPA requires a no-arg constructor.
    public Todo() {}

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: @PrePersist
    //
    // This method runs AUTOMATICALLY right before JPA saves a new entity to the DB.
    // We use it to set createdAt so it's ALWAYS set by the server — never by the client.
    //
    // Other lifecycle hooks you'll encounter later:
    //   @PostLoad    — after the entity is fetched from DB
    //   @PreUpdate   — right before an UPDATE
    //   @PreRemove   — right before a DELETE
    // ══════════════════════════════════════════════════════════════════════════
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

