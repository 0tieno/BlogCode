# Project 04 — User Authentication & JWT 🔐

## What you will learn

- **Why we need authentication** — who is making this request?
- **Passwords and BCrypt** — why you must NEVER store plain text passwords
- **Spring Security** — what it is and how it works
- **JWT (JSON Web Token)** — a stateless way to prove who you are
- **How registration and login work** end-to-end
- **`@Value`** — reading configuration from `application.properties`

---

## The Core Idea — Authentication vs Authorization

| Term | Question | Example |
|------|----------|---------|
| **Authentication** | Who are you? | "I am Alice" (proven with a password or token) |
| **Authorization** | What are you allowed to do? | "Alice can read todos, but only ADMIN can delete users" |

This project covers **authentication** (login/register + JWT).
Project 05 adds **authorization** (protecting specific endpoints).

---

## The Core Idea — Why BCrypt?

**NEVER store passwords as plain text.** If your database is ever hacked,
all your users' passwords are exposed.

BCrypt is a **one-way hashing algorithm**:
```
Input: "mypassword123"
Output: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
```

- You can go from password → hash (fast)
- You CANNOT go from hash → password (impossible)
- To verify: hash the input again and compare hashes

```java
passwordEncoder.encode("password123")          // store this in DB
passwordEncoder.matches("password123", hash)   // true ✓
passwordEncoder.matches("wrongpassword", hash) // false ✗
```

---

## The Core Idea — What is a JWT?

After login, the server gives you a **JWT token** — a signed string that proves your identity.

```
eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhbGljZUB0ZXN0LmNvbSJ9.abc123
      ▲                         ▲                            ▲
   Header                    Payload                     Signature
  (algorithm)             (who you are)            (proves it's real)
```

For every future request, the client sends:
```
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

The server reads the token → verifies the signature → knows who you are.
No database lookup needed on every request. That's "stateless".

---

## Endpoints to test

| Method | URL | Body | Description |
|--------|-----|------|-------------|
| POST | `/api/auth/register` | `{"name":"Alice","email":"alice@test.com","password":"pass1234"}` | Register |
| POST | `/api/auth/login` | `{"email":"alice@test.com","password":"pass1234"}` | Get token |

Both return: `{ "token": "eyJ..." }`

---

## Key vocabulary

| Term | Meaning |
|------|---------|
| **BCrypt** | A slow, secure hashing algorithm for passwords |
| **JWT** | JSON Web Token — a signed string that proves identity |
| **Stateless** | The server doesn't remember you between requests — the token carries everything |
| **`@Value`** | Injects a value from `application.properties` into a field |
| **Secret key** | Used to sign/verify JWTs — must be kept secret and never committed to git |

