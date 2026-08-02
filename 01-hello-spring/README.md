# 01 - Hello Spring

The first stop in the Spring Boot curriculum. This project is deliberately
tiny: its only purpose is to show an absolute beginner what the *smallest
useful* Spring Boot REST application looks like, and to introduce the
vocabulary (dependency injection, beans, auto-configuration, DTOs) used in
every later project.

## What this project teaches

- How a Spring Boot application starts (`@SpringBootApplication` + `main`).
- How Maven's "parent POM" (`spring-boot-starter-parent`) manages dependency
  versions for you.
- The difference between a **domain object** (`Greeting`, a `record`) and a
  **DTO** used purely for the web layer (`GreetingController.GreetingRequest`).
- Layered architecture: **controller -> service interface -> service
  implementation**.
- Constructor-based dependency injection using Lombok's
  `@RequiredArgsConstructor`.
- Basic request validation with `jakarta.validation` (`@NotBlank`, `@Valid`).
- How to write a fast, focused controller test with `@WebMvcTest` and
  `MockMvc` (see `GreetingControllerTest`).

## New concepts introduced here

| Concept | Where it appears |
|---|---|
| `@SpringBootApplication` | `HelloSpringApplication` |
| Java `record` as an immutable value object | `Greeting` |
| Service interface vs. implementation | `GreetingService` / `GreetingServiceImpl` |
| `@Service` stereotype & singleton beans | `GreetingServiceImpl` |
| `@RestController` + `@RequestMapping` | `GreetingController` |
| `@RequiredArgsConstructor` constructor injection | `GreetingController`, `GreetingServiceImpl` |
| Nested DTO record for a request body | `GreetingController.GreetingRequest` |
| `@Valid` + `@NotBlank` request validation | `GreetingController#createGreeting` |
| `@WebMvcTest` + `MockMvc` + `@MockitoBean` | `GreetingControllerTest` |

## Project layout

```
01-hello-spring/
├── pom.xml
├── mvnw / mvnw.cmd
├── README.md
└── src
    ├── main
    │   ├── java/com/blogcode/hellospring
    │   │   ├── HelloSpringApplication.java      <- application entry point
    │   │   ├── model/Greeting.java               <- immutable domain record
    │   │   ├── service/GreetingService.java       <- service interface (contract)
    │   │   ├── service/impl/GreetingServiceImpl.java <- service implementation
    │   │   └── controller/GreetingController.java <- REST endpoints + request DTO
    │   └── resources/application.properties
    └── test
        └── java/com/blogcode/hellospring/controller/GreetingControllerTest.java
```

## Architecture diagram

```
                       HTTP request (JSON)
                             │
                             ▼
                 ┌───────────────────────┐
                 │   GreetingController   │   @RestController
                 │  (web / HTTP layer)    │   - GET  /api/v1/greetings
                 │                        │   - POST /api/v1/greetings
                 │  uses: GreetingRequest │   (nested DTO record)
                 └───────────┬────────────┘
                             │ depends on (constructor injection)
                             ▼
                 ┌───────────────────────┐
                 │    GreetingService     │   interface (contract)
                 │      (contract)        │
                 └───────────┬────────────┘
                             │ implemented by
                             ▼
                 ┌───────────────────────┐
                 │  GreetingServiceImpl   │   @Service
                 │   (business logic)     │
                 └───────────┬────────────┘
                             │ creates
                             ▼
                 ┌───────────────────────┐
                 │       Greeting         │   record (domain/value object)
                 │  (id, message, time)   │
                 └───────────────────────┘
```

## Prerequisites

- **JDK 25**
- **Apache Maven 3.9+** (or use the bundled `mvnw` / `mvnw.cmd`, which simply
  delegate to a Maven installation already on your `PATH`)

## Running the application

On Windows (PowerShell / cmd):

```powershell
cd C:\Users\USER\developer\BlogCode\01-hello-spring
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
cd BlogCode/01-hello-spring
./mvnw spring-boot:run
```

The application starts an embedded Tomcat server on **http://localhost:8080**.

Build an executable jar and run it directly:

```powershell
.\mvnw.cmd clean package
java -jar target/hello-spring.jar
```

Run the tests:

```powershell
.\mvnw.cmd test
```

## Endpoints

### `GET /api/v1/greetings`

Returns the default "Hello, World!" greeting.

```bash
curl -s http://localhost:8080/api/v1/greetings
```

Example response:

```json
{
  "id": 1,
  "message": "Hello, World!",
  "createdAt": "2026-08-02T18:30:00.123Z"
}
```

### `POST /api/v1/greetings`

Creates a personalised greeting from a JSON request body.

```bash
curl -s -X POST http://localhost:8080/api/v1/greetings \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Ada\"}"
```

Example response (`201 Created`):

```json
{
  "id": 2,
  "message": "Hello, Ada!",
  "createdAt": "2026-08-02T18:31:05.456Z"
}
```

Sending a blank name (violates `@NotBlank`) returns `400 Bad Request`:

```bash
curl -s -X POST http://localhost:8080/api/v1/greetings \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"\"}"
```

## Where to go next

Once you're comfortable with the concepts above, move on to
[`02-student-crud-api`](../02-student-crud-api/README.md), which builds on
this exact same layered structure (controller/service/DTO) but adds a real
database (H2), JPA entities, a repository layer, and full CRUD semantics.
