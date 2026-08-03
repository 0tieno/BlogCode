# 02 - Student CRUD API

The second project in the Spring Boot curriculum. Building directly on the
layered structure introduced in
[`01-hello-spring`](../01-hello-spring/README.md), this project adds a real
persistence layer and implements a complete CRUD (Create, Read, Update,
Delete) REST API for managing student records, backed by an in-memory H2
database.

## What this project teaches

- How Spring Data JPA turns a repository **interface** into a working
  database access layer with zero implementation code.
- How to model a database table as a JPA **entity** (`Student`), and why
  entities are mutable classes rather than records.
- The DTO (Data Transfer Object) pattern applied to both **requests**
  (`StudentRequest`) and **responses** (`StudentResponse`), kept separate
  from the entity.
- Request validation with `jakarta.validation` annotations (`@NotBlank`,
  `@Email`, `@Min`, `@Max`) and how violations become structured `400`
  responses.
- Centralised exception handling with `@RestControllerAdvice`, converting
  both business exceptions (`ResourceNotFoundException`) and validation
  failures into a single, consistent `ErrorResponse` JSON shape.
- Transaction management with `@Transactional`, including the "read-only by
  default, opt-in to writes" pattern.
- Loading seed data automatically at startup with `data.sql`.
- Testing a controller in isolation with `@WebMvcTest`, `MockMvc` and
  `@MockitoBean`.

## New concepts introduced here (beyond 01-hello-spring)

| Concept | Where it appears |
|---|---|
| `@Entity` / `@Table` / `@Id` / `@GeneratedValue` | `entity/Student.java` |
| Spring Data `JpaRepository` + derived query methods | `repository/StudentRepository.java` |
| Custom JPQL query with `@Query` | `StudentRepository#searchByKeyword` |
| Request/response DTO separation | `dto/StudentRequest.java`, `dto/StudentResponse.java` |
| Entity <-> DTO mapping utility | `mapper/StudentMapper.java` |
| Bean Validation annotations (`@NotBlank`, `@Email`, `@Min`, `@Max`) | `dto/StudentRequest.java` |
| `@Valid` on controller parameters | `controller/StudentController.java` |
| `@RestControllerAdvice` global exception handling | `exception/GlobalExceptionHandler.java` |
| Custom unchecked exception | `exception/ResourceNotFoundException.java` |
| Uniform error response shape | `exception/ErrorResponse.java` |
| `@Transactional(readOnly = true)` class-level default + method-level overrides | `service/impl/StudentServiceImpl.java` |
| H2 in-memory database + `data.sql` seeding | `application.properties`, `data.sql` |
| `@WebMvcTest` + `@MockitoBean` controller testing | `controller/StudentControllerTest.java` |

## Project layout

```
02-student-crud-api/
├── pom.xml
├── mvnw / mvnw.cmd
├── README.md
└── src
    ├── main
    │   ├── java/com/blogcode/studentcrudapi
    │   │   ├── StudentCrudApiApplication.java     <- application entry point
    │   │   ├── entity/Student.java                 <- JPA entity (table mapping)
    │   │   ├── repository/StudentRepository.java   <- Spring Data JPA repository
    │   │   ├── dto/StudentRequest.java              <- validated inbound DTO
    │   │   ├── dto/StudentResponse.java              <- outbound DTO
    │   │   ├── mapper/StudentMapper.java             <- entity <-> DTO conversion
    │   │   ├── service/StudentService.java           <- service interface (contract)
    │   │   ├── service/impl/StudentServiceImpl.java  <- business logic + transactions
    │   │   ├── controller/StudentController.java     <- REST endpoints
    │   │   └── exception
    │   │       ├── ResourceNotFoundException.java    <- 404-mapped exception
    │   │       ├── ErrorResponse.java                <- uniform error JSON shape
    │   │       └── GlobalExceptionHandler.java       <- @RestControllerAdvice
    │   └── resources
    │       ├── application.properties
    │       └── data.sql                              <- seed data (8 sample students)
    └── test
        └── java/com/blogcode/studentcrudapi/controller/StudentControllerTest.java
```

## Architecture diagram

```
                             HTTP request (JSON)
                                     │
                                     ▼
                     ┌───────────────────────────┐
                     │     StudentController      │  @RestController
                     │      (web / HTTP layer)     │  GET/POST/PUT/DELETE
                     └─────────────┬───────────────┘
                                    │ @Valid StudentRequest
                                    │ (constructor injection)
                                    ▼
                     ┌───────────────────────────┐
                     │       StudentService        │  interface (contract)
                     └─────────────┬───────────────┘
                                    │ implemented by
                                    ▼
                     ┌───────────────────────────┐
                     │    StudentServiceImpl       │  @Service @Transactional
                     │     (business logic)         │
                     └───────┬───────────────┬───────┘
                              │               │
                   uses       │               │  uses
                              ▼               ▼
                ┌─────────────────────┐  ┌─────────────────────┐
                │    StudentMapper     │  │  StudentRepository   │  extends JpaRepository
                │ (entity <-> DTO)     │  │ (Spring Data proxy)  │
                └─────────────────────┘  └──────────┬───────────┘
                                                      │
                                                      ▼
                                          ┌─────────────────────┐
                                          │   Student (@Entity)  │
                                          └──────────┬───────────┘
                                                      │
                                                      ▼
                                          ┌─────────────────────┐
                                          │   H2 in-memory DB     │
                                          │  (table: student)     │
                                          │  seeded by data.sql    │
                                          └─────────────────────┘

  Any exception thrown by StudentService/StudentController is intercepted by:
                     ┌───────────────────────────┐
                     │   GlobalExceptionHandler    │  @RestControllerAdvice
                     │  -> ErrorResponse (JSON)     │
                     └───────────────────────────┘
```

## Prerequisites

- **JDK 25**
- **Apache Maven 3.9+** (or use the bundled `mvnw` / `mvnw.cmd`)

## Running the application

On Windows (PowerShell / cmd):

```powershell
cd C:\Users\USER\developer\BlogCode\02-student-crud-api
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
cd BlogCode/02-student-crud-api
./mvnw spring-boot:run
```

The application starts on **http://localhost:8080** and immediately seeds 8
sample students from `data.sql`. The H2 web console is available at
**http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:studentdb`,
user: `sa`, empty password).

Build an executable jar and run it directly:

```powershell
.\mvnw.cmd clean package
java -jar target/student-crud-api.jar
```

Run the tests:

```powershell
.\mvnw.cmd test
```

## Endpoints

### `GET /api/v1/students`

Lists every student.

```bash
curl -s http://localhost:8080/api/v1/students
```

### `GET /api/v1/students/{id}`

Fetches a single student by id.

```bash
curl -s http://localhost:8080/api/v1/students/1
```

Returns `404 Not Found` with an `ErrorResponse` body if the id does not
exist:

```bash
curl -s http://localhost:8080/api/v1/students/999
```

### `GET /api/v1/students/search?keyword=john`

Searches students whose first or last name contains the keyword
(case-insensitive).

```bash
curl -s "http://localhost:8080/api/v1/students/search?keyword=john"
```

### `GET /api/v1/students/course/{course}`

Lists students enrolled in an exact course name.

```bash
curl -s "http://localhost:8080/api/v1/students/course/Computer%20Science"
```

### `POST /api/v1/students`

Creates a new student.

```bash
curl -s -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d "{\"firstName\": \"Grace\", \"lastName\": \"Hopper\", \"email\": \"grace.hopper@example.com\", \"course\": \"Computer Science\", \"age\": 30}"
```

Example response (`201 Created`):

```json
{
  "id": 9,
  "firstName": "Grace",
  "lastName": "Hopper",
  "email": "grace.hopper@example.com",
  "course": "Computer Science",
  "age": 30
}
```

Sending an invalid payload (e.g. blank `firstName`, malformed `email`)
returns `400 Bad Request` with field-level error messages:

```bash
curl -s -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -d "{\"firstName\": \"\", \"lastName\": \"Hopper\", \"email\": \"not-an-email\", \"course\": \"\", \"age\": 3}"
```

### `PUT /api/v1/students/{id}`

Fully updates an existing student.

```bash
curl -s -X PUT http://localhost:8080/api/v1/students/1 \
  -H "Content-Type: application/json" \
  -d "{\"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", \"course\": \"Data Science\", \"age\": 22}"
```

### `DELETE /api/v1/students/{id}`

Deletes a student. Returns `204 No Content` on success.

```bash
curl -s -X DELETE http://localhost:8080/api/v1/students/1 -w "%{http_code}\n"
```

## Error response shape

Every error produced by this API (validation failure, missing resource, or
unexpected server error) follows the same JSON shape, produced by
`GlobalExceptionHandler`:

```json
{
  "status": 404,
  "message": "Resource not found",
  "errors": ["Student not found with id: 999"],
  "timestamp": "2026-08-02T18:45:00.123Z"
}
```

## Where this fits in the curriculum

This project assumes familiarity with the concepts taught in
[`01-hello-spring`](../01-hello-spring/README.md) (application bootstrap,
`@RestController`, service interfaces, constructor injection, DTOs) and adds
the persistence, validation and error-handling layers needed for a
realistic, production-shaped CRUD API.
