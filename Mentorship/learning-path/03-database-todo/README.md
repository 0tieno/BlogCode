# Project 03 — Todo with a Real Database 🗄️

## What you will learn

- **JPA and Hibernate** — what they are and why they exist
- **`@Entity`** — turning a Java class into a database table
- **`@Id`, `@GeneratedValue`** — primary keys and auto-increment
- **`@Column`** — controlling column properties (nullable, unique, etc.)
- **`@ManyToOne` / `@JoinColumn`** — relationships between tables
- **`@PrePersist`** — running code automatically before saving
- **`JpaRepository`** — Spring's magic interface that writes SQL for you
- **Why Lombok?** — removing getters/setters boilerplate with `@Getter @Setter`
- **H2 in-memory database** — a database that runs inside your app (no install needed)

---

## The Core Idea — What is JPA?

**Problem:** Java uses Objects. Databases use Tables. They are completely different.

**JPA (Java Persistence API)** is the bridge. It maps your Java class to a database table:

```
Java Class           ↔    Database Table
──────────────────────────────────────────
public class Todo     ↔    CREATE TABLE todos (
  Long id             ↔      id BIGINT PRIMARY KEY AUTO_INCREMENT,
  String title        ↔      title VARCHAR NOT NULL,
  boolean completed   ↔      completed BOOLEAN NOT NULL,
  LocalDateTime at    ↔      created_at TIMESTAMP
)                          )
```

You write Java. JPA generates the SQL. You almost never write SQL directly.

---

## The Core Idea — What is JpaRepository?

Instead of writing SQL queries, you extend `JpaRepository` and Spring gives you
methods for free:

```java
todoRepository.findAll()          // SELECT * FROM todos
todoRepository.findById(1L)       // SELECT * FROM todos WHERE id = 1
todoRepository.save(todo)         // INSERT INTO todos ... or UPDATE ...
todoRepository.delete(todo)       // DELETE FROM todos WHERE id = ...
todoRepository.findByTitle("Buy") // Spring GENERATES this from the method name!
```

---

## H2 — the learning database

H2 is an in-memory database that:
- Runs INSIDE your Java app — nothing to install
- Resets every time your app restarts (that's fine for learning)
- Has a browser UI at `http://localhost:8082/h2-console`

When you're ready for a real project, you just swap H2 for PostgreSQL
by changing 3 lines in `application.properties`.

---

## Endpoints to test

Same as Project 02. The API hasn't changed — only the storage layer did.
This is the power of layered architecture: the controller and service stay the same.

---

## Key vocabulary

| Term | Meaning |
|------|---------|
| **JPA** | Java Persistence API — standard for mapping Java objects to database tables |
| **Hibernate** | The most popular JPA implementation (Spring uses it under the hood) |
| **Entity** | A Java class mapped to a database table |
| **Repository** | An interface that gives you database operations (save, find, delete) |
| **H2** | A lightweight in-memory database, perfect for learning and testing |
| **Lombok** | A library that generates boilerplate code (getters, setters, constructors) |

