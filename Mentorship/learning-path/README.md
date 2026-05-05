# Spring Boot Learning Path 🚀

A step-by-step series of projects, each one building on the previous.
Work through them **in order** — every project adds one new layer of knowledge.

---

## How to use this

1. Open one project folder at a time
2. Read its `README.md` **first** (it explains what you're about to learn)
3. Read the source code — every line has a comment explaining the WHY
4. Run it and test it with Postman
5. Move to the next project only when you understand the current one

---

## The Series

| Step | Folder | What you will learn |
|------|--------|---------------------|
| 1 | `01-hello-world-api/` | What is Spring Boot? Controllers, HTTP methods, path variables |
| 2 | `02-in-memory-todo/` | Layers (Controller → Service), DTOs, input validation |
| 3 | `03-database-todo/` | Databases, JPA entities, repositories, H2 |
| 4 | `04-auth-jwt/` | Passwords, BCrypt hashing, JWT tokens, Spring Security |
| 5 | `05-secured-todo-api/` | JWT filter, SecurityFilterChain, ownership/security checks |
| 6 | `06-testing/` | Unit tests, Mockito mocking, the AAA pattern |

---

## Prerequisites

- Java 21+ installed
- Maven installed (or use the included `mvnw`)
- Postman or any API client for testing
- An IDE (IntelliJ IDEA recommended)

> **Note:** Projects 01–05 use an **in-memory H2 database** — no PostgreSQL setup needed.
> The concepts are identical. Once you understand them here, switching to PostgreSQL
> is just changing 3 lines in `application.properties`.

