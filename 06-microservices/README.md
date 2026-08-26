# 06 - Microservices

A five-service, Spring-Boot-4.1.0-based microservices system built to
teach absolute beginners the patterns that distinguish "several small
Spring Boot apps" from an actual **microservices architecture**: service
discovery, gateway routing, inter-service HTTP calls, resilience, and
event-driven decoupling.

This project is a **multi-module Maven build** - one root `pom.xml`
aggregates five independently runnable Spring Boot applications, each its
own Maven module with its own `pom.xml`.

---

## 1. What this project teaches

Module 5 (`05-ecommerce-api`) taught you how to build one, well-layered
Spring Boot application backed by a single database. Module 6 asks a
different question: **what happens when your system needs to be more than
one application?** It walks through the standard toolkit for splitting a
system into independently deployable services while still being able to
reason about it as a whole:

| Concept                        | Where it's demonstrated                                             | Why it matters |
|---------------------------------|-----------------------------------------------------------------------|-----------------|
| Multi-module Maven build         | root `pom.xml` + 5 child modules                                     | one parent POM centralizes shared versions; each module only declares which dependencies it needs |
| Service discovery (Eureka)       | `service-registry` module; every other service registers with it      | services find each other by logical name, not hardcoded host:port |
| API Gateway / edge routing       | `api-gateway` module                                                  | a single, stable entry point (port 8080) hides internal service topology from clients |
| Declarative HTTP clients (Feign) | `student-service`'s `CourseClient`                                    | calling another service looks like calling a local Java method |
| Circuit breaker / resilience     | `student-service`'s `ResilienceConfig` + `CourseClientFallback`       | a struggling downstream service degrades gracefully instead of cascading failure |
| Database-per-service             | `student-service` (`student_db`) and `course-service` (`course_db`)   | each service owns its data exclusively; no cross-service foreign keys |
| No cross-service JPA relationships | `Student.enrolledCourseId` is a plain `Long`, not a `@ManyToOne`     | a "relationship" across services must be resolved over the network, not the database |
| No shared DTO library            | `student-service`'s own local `CourseDto` (a subset of course-service's fields) | avoids deployment coupling between independently-versioned services |
| Event-driven processing (simulated) | `notification-service`'s `OrderPlacedEvent` + `@Async @EventListener` | decouples "something happened" from "how we react to it", the same shape a real message broker provides |
| Database-free service            | `notification-service` (in-memory `ConcurrentHashMap` store)          | not every service needs persistence; some are legitimately simple |

### New concepts vs. module 5

Module 5 was a **monolith**: one application, one database, JPA
relationships modeling everything. Module 6 deliberately contrasts with
that:

- Instead of one `pom.xml`, there are **six** (one parent + five children).
- Instead of one database, there are **two independent Postgres instances**
  plus one database-free service.
- Instead of a JPA `@ManyToOne`, cross-service "relationships" are resolved
  by an HTTP call (Feign) at request time.
- Instead of calling a service's Java code directly, everything goes
  through a **gateway** and/or **service discovery**.
- Instead of `@Async` calling a local `EmailService` (module 5), a
  **simulated domain event** is published and handled independently,
  foreshadowing message brokers (Kafka/RabbitMQ) without requiring one.

---

## 2. Architecture

```
                                   ┌────────────────────────┐
                                   │   service-registry      │
                                   │   (Eureka Server)       │
                                   │   port 8761              │
                                   └────────────▲─────────────┘
                                                │ register / discover
                        ┌───────────────────────┼───────────────────────┐
                        │                       │                       │
                        │                       │                       │
              ┌─────────┴────────┐   ┌──────────┴─────────┐   ┌─────────┴──────────┐
   curl ───▶  │    api-gateway    │   │  student-service    │   │   course-service    │
              │   port 8080       │──▶│   port 8081         │──▶│   port 8082          │
              │  (routes by path) │   │ (Feign + circuit    │   │                      │
              └───────────────────┘   │  breaker)           │   │                      │
                                      └──────────┬──────────┘   └──────────┬───────────┘
                                                 │                          │
                                       ┌─────────▼─────────┐     ┌──────────▼──────────┐
                                       │ postgres-student    │     │ postgres-course       │
                                       │ (student_db :5433)  │     │ (course_db  :5434)   │
                                       └────────────────────┘     └──────────────────────┘

              ┌────────────────────────────────────────────────────┐
   curl ───▶  │              notification-service                   │
              │              port 8083 (no database)                │
              │  REST endpoint publishes OrderPlacedEvent ──▶       │
              │  @Async @EventListener processes it, stores in a    │
              │  ConcurrentHashMap                                  │
              └────────────────────────────────────────────────────┘
```

Request flow example (`GET /api/students/1` through the gateway):

```
curl → api-gateway:8080 (Path=/api/students/** matched)
     → resolves "student-service" via Eureka → student-service:8081
     → StudentController → StudentServiceImpl
         → StudentRepository (student_db, local JPA)
         → CourseClient (Feign) → course-service:8082 (resolved via Eureka)
             → CourseController → CourseServiceImpl → CourseRepository (course_db)
         → if course-service is down/slow: circuit breaker opens,
           CourseClientFallback returns a degraded placeholder instead
     → merged StudentDto (with enrolledCourse) returned to caller
```

---

## 3. Project layout

```
06-microservices/
├── pom.xml                     # parent aggregator POM (Spring Boot 4.1.0, Java 25, Spring Cloud 2025.1.2 BOM)
├── mvnw / mvnw.cmd              # Maven wrapper launcher scripts
├── docker-compose.yml           # two Postgres instances: postgres-student, postgres-course
├── service-registry/            # Eureka server
│   ├── pom.xml
│   └── src/main/java/.../registry/ServiceRegistryApplication.java
│   └── src/main/resources/application.yml
├── api-gateway/                 # Spring Cloud Gateway (WebMVC flavor)
│   ├── pom.xml
│   └── src/main/java/.../gateway/
│       ├── ApiGatewayApplication.java
│       └── config/GatewayLoggingConfig.java
│   └── src/main/resources/application.yml
├── student-service/             # CRUD + Feign client + circuit breaker
│   ├── pom.xml
│   └── src/main/java/.../student/
│       ├── StudentServiceApplication.java
│       ├── domain/Student.java
│       ├── dto/{StudentDto,StudentRequest,CourseDto}.java
│       ├── client/{CourseClient,CourseClientFallback}.java
│       ├── config/ResilienceConfig.java
│       ├── repository/StudentRepository.java
│       ├── service/StudentService.java
│       ├── service/impl/StudentServiceImpl.java
│       ├── controller/StudentController.java
│       └── exception/{ResourceNotFoundException,ErrorResponse,GlobalExceptionHandler}.java
│   └── src/main/resources/application.yml
│   └── src/test/java/.../student/service/StudentServiceImplTest.java
├── course-service/              # CRUD for courses
│   ├── pom.xml
│   └── src/main/java/.../course/  (same shape as student-service, minus Feign)
│   └── src/main/resources/application.yml
│   └── src/test/java/.../course/service/CourseServiceImplTest.java
└── notification-service/        # in-memory, event-driven notifications
    ├── pom.xml
    └── src/main/java/.../notification/
        ├── NotificationServiceApplication.java
        ├── domain/{Notification,NotificationChannel}.java
        ├── dto/{NotificationDto,NotificationRequest,OrderPlacedEventRequest}.java
        ├── event/{OrderPlacedEvent,NotificationEventListener}.java
        ├── service/NotificationService.java
        ├── service/impl/NotificationServiceImpl.java
        ├── controller/{NotificationController,NotificationEventController}.java
        ├── config/AsyncConfig.java
        └── exception/{ErrorResponse,GlobalExceptionHandler}.java
    └── src/main/resources/application.yml
    └── src/test/java/.../notification/service/NotificationServiceImplTest.java
```

---

## 4. Prerequisites

- Java 25 JDK
- Maven 3.9+ (or use the bundled `mvnw`/`mvnw.cmd`)
- Docker + Docker Compose (for PostgreSQL)

> This build was authored against Spring Boot **4.1.0** / Spring Cloud
> **2025.1.2**. If your local JDK is older than 25, override the compiler
> release for a local trial run, e.g.
> `mvnw.cmd "-Dmaven.compiler.release=21" clean install`. The `pom.xml`
> files themselves correctly declare Java 25 as required by the curriculum.

---

## 5. Run instructions

All commands below assume PowerShell and the `06-microservices` directory
as the current working directory.

### 5.1 Start the databases

```powershell
docker compose up -d
```

This starts `postgres-student` (port 5433, `student_db`) and
`postgres-course` (port 5434, `course_db`). `notification-service` needs no
database at all.

### 5.2 Build every module

```powershell
.\mvnw.cmd clean install
```

### 5.3 Start every service, **in this order** (each in its own terminal)

```powershell
# 1. Service registry must be up first - everything else registers with it.
cd service-registry; ..\mvnw.cmd spring-boot:run

# 2. Data-backed services (order between these two doesn't matter).
cd course-service; ..\mvnw.cmd spring-boot:run
cd student-service; ..\mvnw.cmd spring-boot:run

# 3. Gateway (needs Eureka up so it can resolve lb:// routes).
cd api-gateway; ..\mvnw.cmd spring-boot:run

# 4. Notification service (independent - no dependencies on the others).
cd notification-service; ..\mvnw.cmd spring-boot:run
```

Ports: `service-registry` 8761, `course-service` 8082, `student-service`
8081, `api-gateway` 8080, `notification-service` 8083.

Visit `http://localhost:8761` to see the Eureka dashboard and confirm every
service has registered.

---

## 6. Endpoints and curl examples

All examples below go **through the gateway** (port 8080) where a route
exists; direct-to-service calls are also shown for comparison.

### 6.1 Courses (via gateway → course-service)

```powershell
# Create a course
curl -X POST http://localhost:8080/api/courses `
  -H "Content-Type: application/json" `
  -d '{"title":"Algorithms","description":"Intro to algorithms","instructor":"Dr. Turing","credits":4}'

# List all courses
curl http://localhost:8080/api/courses

# Get one course
curl http://localhost:8080/api/courses/1

# Update a course
curl -X PUT http://localhost:8080/api/courses/1 `
  -H "Content-Type: application/json" `
  -d '{"title":"Advanced Algorithms","description":"Graph theory and beyond","instructor":"Dr. Turing","credits":5}'

# Delete a course
curl -X DELETE http://localhost:8080/api/courses/1
```

### 6.2 Students (via gateway → student-service → Feign → course-service)

```powershell
# Create a student enrolled in course id 1
curl -X POST http://localhost:8080/api/students `
  -H "Content-Type: application/json" `
  -d '{"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com","enrolledCourseId":1}'

# List all students (each response is enriched with live course details)
curl http://localhost:8080/api/students

# Get one student
curl http://localhost:8080/api/students/1

# Update a student
curl -X PUT http://localhost:8080/api/students/1 `
  -H "Content-Type: application/json" `
  -d '{"firstName":"Ada","lastName":"Lovelace","email":"ada@example.com","enrolledCourseId":2}'

# Delete a student
curl -X DELETE http://localhost:8080/api/students/1
```

Stop `course-service` and repeat the `GET /api/students/1` call: thanks to
`CourseClientFallback` and the resilience4j circuit breaker configured in
`ResilienceConfig`, the student is still returned - just with a
degraded/placeholder `enrolledCourse` instead of a hard failure.

### 6.3 Notifications (direct - notification-service is not routed through the gateway)

```powershell
# Send a notification directly
curl -X POST http://localhost:8083/api/v1/notifications `
  -H "Content-Type: application/json" `
  -d '{"recipient":"ada@example.com","message":"Welcome to the platform!","channel":"EMAIL"}'

# List all notifications
curl http://localhost:8083/api/v1/notifications

# List notifications for one recipient
curl http://localhost:8083/api/v1/notifications/recipients/ada@example.com

# Simulate an inbound "order placed" event (as if from a message broker) -
# returns 202 Accepted immediately; the notification is created
# asynchronously by NotificationEventListener a moment later.
curl -X POST http://localhost:8083/api/v1/notifications/events/order-placed `
  -H "Content-Type: application/json" `
  -d '{"orderId":42,"customerEmail":"ada@example.com","totalAmount":99.90}'

# Confirm it arrived (poll again after a second or two)
curl http://localhost:8083/api/v1/notifications/recipients/ada@example.com
```

### 6.4 Service registry dashboard

```powershell
# Machine-readable list of registered instances
curl http://localhost:8761/eureka/apps -H "Accept: application/json"
```

### 6.5 Actuator health checks (every service)

```powershell
curl http://localhost:8761/actuator/health   # service-registry
curl http://localhost:8080/actuator/health   # api-gateway
curl http://localhost:8081/actuator/health   # student-service
curl http://localhost:8082/actuator/health   # course-service
curl http://localhost:8083/actuator/health   # notification-service
curl http://localhost:8081/actuator/health/circuitbreakers  # resilience4j status
```

---

## 7. Per-service notes

### service-registry
Plain Eureka server (`@EnableEurekaServer`). Configured with
`register-with-eureka: false` and `fetch-registry: false` because the
registry itself has no need to discover or register with anyone - it *is*
the registry. See `ServiceRegistryApplication.java`.

### api-gateway
Uses the **WebMVC flavor** of Spring Cloud Gateway
(`spring-cloud-starter-gateway-server-webmvc`), consistent with this
curriculum's standardization on `spring-boot-starter-webmvc` everywhere
(never the reactive/WebFlux stack). Routes are declared under
`spring.cloud.gateway.server.webmvc.routes` in `application.yml` using the
classic `Path=` predicate shorthand and `lb://` URIs resolved through
Eureka + Spring Cloud LoadBalancer. `GatewayLoggingConfig` adds a servlet
`Filter` that logs every incoming request path, a simple example of a
cross-cutting gateway concern.

### student-service
Owns `student_db` exclusively. `Student.enrolledCourseId` is a plain
`Long`, never a JPA relationship, because course-service's database is
not reachable from here - see the Javadoc on `Student` for the full
rationale. `CourseClient` (OpenFeign) calls course-service by its logical
Eureka name; `ResilienceConfig` configures a resilience4j circuit breaker
(50% failure threshold over a 5-call window, 5s open state) so that
`CourseClientFallback` takes over automatically when course-service is
unreachable, keeping student lookups available in a degraded mode instead
of failing outright.

### course-service
Owns `course_db` exclusively. Deliberately simple CRUD - no relationships,
no external calls - so students can focus on the multi-module/Eureka setup
without extra JPA complexity on this side.

### notification-service
The only service with **no database**: `NotificationServiceImpl` stores
notifications in a `ConcurrentHashMap`, so data is lost on restart - an
explicit, documented trade-off appropriate for this teaching example.
`NotificationEventController` simulates receiving an external domain event
(what would, in production, arrive via Kafka/RabbitMQ) by publishing a
plain-record `OrderPlacedEvent` through Spring's built-in
`ApplicationEventPublisher`; `NotificationEventListener` reacts to it with
`@Async @EventListener`, running on a separate thread so the originating
HTTP call returns immediately (`202 Accepted`) while processing continues
in the background - the same request/response decoupling a real message
broker provides.

---

## 8. Design decisions worth calling out

- **No shared DTO/model library.** `student-service` keeps its own local
  `CourseDto` instead of depending on a `course-service-api` artifact.
  Sharing code between services creates a hidden deployment coupling: a
  change to a shared class would force every consumer to redeploy in
  lock-step, defeating the purpose of splitting services in the first
  place.
- **Database per service.** `student_db` and `course_db` are two entirely
  separate Postgres instances (ports 5433/5434) with separate credentials.
  Neither service can query the other's tables directly - all
  cross-service reads happen over HTTP (Feign).
- **WebMVC everywhere, including the gateway.** Rather than mixing the
  reactive Spring Cloud Gateway with servlet-based services, this project
  uses the newer WebMVC-native Gateway server so every module in this
  curriculum shares the same programming model (`spring-boot-starter-webmvc`).
- **Simulated events over a real broker.** A message broker (Kafka/
  RabbitMQ) is the production-grade way to connect
  `05-ecommerce-api`/`student-service` to `notification-service`, but
  installing and configuring one is a significant extra infrastructure
  burden for a beginner curriculum. Publishing an in-process
  `ApplicationEvent` from a REST endpoint teaches the *shape* of
  event-driven decoupling (publish now, process later, asynchronously)
  without that overhead.
- **Circuit breaker configured explicitly in code.** `ResilienceConfig`
  spells out the failure-rate threshold, sliding window size, and open-state
  duration as an explicit `@Bean`, rather than relying only on YAML
  defaults, so the resilience behaviour is visible and easy to read
  alongside the Feign client it protects.

---

## 9. File reference

| File | Purpose |
|------|---------|
| `pom.xml` | Parent aggregator POM: Spring Boot 4.1.0 parent, Java 25, Spring Cloud 2025.1.2 BOM import, shared Lombok/test deps |
| `docker-compose.yml` | Two independent Postgres instances (student, course) |
| `mvnw` / `mvnw.cmd` | Maven wrapper launcher scripts |
| `service-registry/src/main/java/.../ServiceRegistryApplication.java` | `@EnableEurekaServer` entry point |
| `api-gateway/src/main/java/.../ApiGatewayApplication.java` | Gateway entry point |
| `api-gateway/src/main/java/.../config/GatewayLoggingConfig.java` | Servlet filter logging every gateway request |
| `api-gateway/src/main/resources/application.yml` | `spring.cloud.gateway.server.webmvc.routes` path-based routing rules |
| `student-service/.../domain/Student.java` | JPA entity; `enrolledCourseId` is a plain `Long`, not a relationship |
| `student-service/.../client/CourseClient.java` | Feign declarative HTTP client to course-service |
| `student-service/.../client/CourseClientFallback.java` | Circuit-breaker fallback implementation |
| `student-service/.../config/ResilienceConfig.java` | Explicit resilience4j circuit breaker configuration |
| `student-service/.../service/impl/StudentServiceImpl.java` | Enriches students with live course data via Feign |
| `course-service/.../domain/Course.java` | Simple JPA entity, owns `course_db` exclusively |
| `notification-service/.../domain/Notification.java` | Plain in-memory POJO (no JPA) |
| `notification-service/.../event/OrderPlacedEvent.java` | Simulated domain event payload |
| `notification-service/.../event/NotificationEventListener.java` | `@Async @EventListener` asynchronous event handler |
| `notification-service/.../controller/NotificationEventController.java` | Publishes simulated events over REST |
| `notification-service/.../service/impl/NotificationServiceImpl.java` | In-memory `ConcurrentHashMap`-backed store |

---

## 10. Testing

Each data-backed module includes a plain Mockito unit test for its service
layer (`CourseServiceImplTest`, `StudentServiceImplTest`), and
`notification-service` includes `NotificationServiceImplTest` covering its
in-memory store. Run every module's tests from the root:

```powershell
.\mvnw.cmd test
```

None of these tests require Eureka, Postgres, or any other running service
- they mock their dependencies (`StudentRepository`, `CourseClient`, etc.)
directly, so the whole test suite runs in seconds with no infrastructure.
