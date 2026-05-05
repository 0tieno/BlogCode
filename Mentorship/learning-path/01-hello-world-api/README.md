# Project 01 — Hello World API 👋

## What you will learn

- What Spring Boot actually is and why it exists
- How a web request travels from browser → your code → back
- `@RestController` and `@RequestMapping`
- HTTP methods: GET and POST
- `@PathVariable` and `@RequestParam`
- `ResponseEntity` — returning proper HTTP status codes
- Records as simple data containers

---

## The Core Idea

Before Spring Boot, writing a web server in Java required hundreds of lines of
configuration. Spring Boot removes all of that. You focus on your code;
Spring Boot handles the rest.

```
Browser/Postman
      │
      │  HTTP Request (GET /greet/Alice)
      ▼
GreetingController   ← @RestController — Spring finds and runs this
      │
      │  returns "Hello, Alice!"
      ▼
Browser/Postman receives the response
```

---

## Endpoints to test

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/greet` | Returns "Hello, World!" |
| GET | `/greet/{name}` | Returns "Hello, {name}!" |
| GET | `/greet/search?name=Alice` | Same but with a query param |
| POST | `/greet` | Send a JSON body, get a greeting back |
| GET | `/status` | Shows you what different HTTP status codes look like |

---

## How to run

```bash
./mvnw spring-boot:run
```

Then open Postman and try the endpoints above.

---

## Key vocabulary

| Term | Meaning |
|------|---------|
| **Endpoint** | A URL your API responds to |
| **@RestController** | Marks a class as a controller that returns JSON/text |
| **@GetMapping** | This method handles GET requests |
| **@PostMapping** | This method handles POST requests |
| **@PathVariable** | A variable embedded IN the URL path: `/greet/{name}` |
| **@RequestParam** | A variable in the query string: `/greet?name=Alice` |
| **ResponseEntity** | A wrapper that lets you control the HTTP status code AND body |
| **HTTP Status** | A number telling the client what happened: 200=OK, 404=Not Found, etc. |

