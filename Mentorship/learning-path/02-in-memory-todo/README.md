# Project 02 — In-Memory Todo List 📝

## What you will learn

- **Why layers exist** — Controller → Service → (Storage)
- **Dependency Injection** — Spring wires your classes together
- **Constructor injection** — the correct way to inject in Spring
- **DTOs (Data Transfer Objects)** — separating "what the API sees" from "what the code uses"
- **Input validation** — `@Valid`, `@NotBlank`, `@Size` and why you must never trust user input
- **Proper HTTP status codes** — `201 Created`, `404 Not Found`, `204 No Content`

---

## The Core Idea — Layers

Without layers, you'd put ALL your logic inside the controller. That becomes messy fast.
Layers separate concerns so each class has exactly ONE job:

```
Request
   │
   ▼
┌─────────────────────────────────────┐
│  TodoController  (Layer 1)           │  "I receive requests and send responses"
│  Knows about: HTTP, JSON, status codes│
└──────────────┬──────────────────────┘
               │  calls
               ▼
┌─────────────────────────────────────┐
│  TodoService  (Layer 2)              │  "I contain the business logic"
│  Knows about: rules, data, validation│
└──────────────┬──────────────────────┘
               │  calls
               ▼
┌─────────────────────────────────────┐
│  List<Todo>   (Storage)              │  "I store the data" (in memory for now)
└─────────────────────────────────────┘
```

**Rule of thumb:** Controllers should be thin. All real logic goes in the Service.

---

## The Core Idea — DTOs

You have two types of objects for the same "Todo":

| Class | Direction | What it contains |
|-------|-----------|-----------------|
| `TodoRequest` | Client → Server | What the CLIENT sends (title, description) |
| `TodoResponse` | Server → Client | What the SERVER returns (id, title, description, createdAt) |
| `Todo` (model) | Internal | The actual data stored inside the service |

Why separate them?
- The client shouldn't see internal fields (like passwords)
- The client shouldn't be able to set internal fields (like `id` or `createdAt`)

---

## Endpoints to test

| Method | URL | Body | Description |
|--------|-----|------|-------------|
| GET | `/api/todos` | — | Get all todos |
| GET | `/api/todos/{id}` | — | Get one todo by ID |
| POST | `/api/todos` | `{"title":"Buy milk","description":"2 litres"}` | Create a todo |
| PUT | `/api/todos/{id}` | `{"title":"Updated","description":"..."}` | Update a todo |
| DELETE | `/api/todos/{id}` | — | Delete a todo |

Try sending a POST with an empty title — you'll get a 400 error because of `@NotBlank`.

---

## Key vocabulary

| Term | Meaning |
|------|---------|
| **Service** | A class annotated with `@Service`. Contains business logic. |
| **Dependency Injection** | Spring creates your objects and "injects" their dependencies for you |
| **Constructor injection** | Receiving dependencies through the constructor (recommended way) |
| **DTO** | Data Transfer Object — a simple class/record for moving data in/out of the API |
| **@Valid** | Tells Spring to run validation on the incoming request body |
| **@NotBlank** | Validation: the field must not be null or empty |

