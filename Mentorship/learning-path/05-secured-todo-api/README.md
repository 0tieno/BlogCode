# Project 05 — Secured Todo API 🛡️

## What you will learn

- **SecurityFilterChain** — telling Spring which endpoints need a token
- **JwtAuthFilter** — intercepting every request and reading the token
- **`SecurityContextHolder`** — Spring's "current user" storage
- **`UserDetailsService`** — loading a user from the database for Spring Security
- **`OncePerRequestFilter`** — a filter that runs exactly once per request
- **IDOR vulnerability** — what it is and how to prevent it with ownership checks

---

## How JWT protection works end-to-end

```
1. User logs in           POST /api/auth/login
                          ← receives: { "token": "eyJ..." }

2. User calls a protected endpoint:
   GET /api/todos
   Headers: Authorization: Bearer eyJ...
                    │
                    ▼
3. JwtAuthFilter  (runs before any controller)
   - Reads "Authorization: Bearer eyJ..." header
   - Extracts the token
   - Validates the signature and expiry
   - Reads the email from the token
   - Loads the User from the database
   - Puts the user into SecurityContextHolder   ← "This is the logged-in user"
                    │
                    ▼
4. Spring Security checks the rules:
   - /api/auth/** → allowed without token  (permit all)
   - /api/todos/** → requires authentication ← passes because filter set the user
                    │
                    ▼
5. TodoController runs
   TodoService reads the email from SecurityContextHolder
   Queries DB: "give me todos WHERE user_id = alice's id"
```

---

## The ownership check — what is an IDOR?

**IDOR = Insecure Direct Object Reference**

Imagine Alice has todo #5. Bob sends: `DELETE /api/todos/5`
Bob doesn't own todo #5 — but if your code just does `todoRepository.findById(5)`,
it will find it and delete it. That's an IDOR vulnerability.

**The fix:**
```java
// Don't just find by id — find by id AND user:
if (!todo.getUser().getId().equals(loggedInUser.getId())) {
    throw new RuntimeException("Todo not found"); // same message as "not found"
    // (Don't tell Bob that Todo #5 exists — it's none of his business)
}
```

---

## Endpoints to test

| Method | URL | Auth? | Description |
|--------|-----|-------|-------------|
| POST | `/api/auth/register` | No | Register |
| POST | `/api/auth/login` | No | Get token |
| GET | `/api/todos` | ✅ Token | Get your todos |
| POST | `/api/todos` | ✅ Token | Create a todo |
| PUT | `/api/todos/{id}` | ✅ Token | Update your todo |
| PATCH | `/api/todos/{id}/complete` | ✅ Token | Toggle completed |
| DELETE | `/api/todos/{id}` | ✅ Token | Delete your todo |

**How to add the token in Postman:**
1. Headers tab → Key: `Authorization` → Value: `Bearer eyJ...your token...`

