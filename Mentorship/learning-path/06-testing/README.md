# Project 06 — Testing 🧪

## What you will learn

- **Unit tests vs Integration tests** — what's the difference and when to use each
- **JUnit 5** — the testing framework built into Spring Boot
- **Mockito** — creating fake (mock) objects to isolate the class you're testing
- **`@Mock` and `@InjectMocks`** — Mockito annotations
- **`when().thenReturn()`** — telling a mock what to return
- **`verify()`** — checking a mock method was actually called
- **The AAA pattern** — Arrange, Act, Assert
- **`assertThat()`** — AssertJ assertions (more readable than JUnit's `assertEquals`)
- **`assertThatThrownBy()`** — testing that exceptions are thrown
- **`@BeforeEach` / `@AfterEach`** — setup and teardown before/after each test
- **Testing security** — simulating a logged-in user in unit tests
- **`@WebMvcTest`** — integration tests for HTTP controllers (MockMvc)
- **`@MockBean`** — registering mocks inside the Spring context
- **`@WithMockUser`** — simulating an authenticated user in controller tests
- **Typed exception assertions** — asserting `isInstanceOf(TodoNotFoundException.class)` not just `RuntimeException`

---

## The Core Idea — Why test?

> "Code that isn't tested is code that doesn't work — you just don't know it yet."

Tests give you confidence to change code. Without tests, every change risks
breaking something silently. With tests, you get immediate red/green feedback.

---

## Unit Test vs Integration Test

| | Unit Test | Integration Test |
|---|---|---|
| What | Tests ONE class in isolation | Tests multiple classes working together |
| Database needed? | ❌ No | ❌ No (mocks services) |
| HTTP layer tested? | ❌ No | ✅ Yes (full request/response) |
| Speed | ⚡ Milliseconds | ⚡ Fast (no real server) |
| How | Mock all dependencies | `@WebMvcTest` + `@MockBean` |
| When | For every method | For every endpoint |

**Rule:** Write both. Unit tests check your logic. Controller tests check your HTTP contract.

---

## The Two Types of Tests in This Project

### 1. Service Unit Tests (in `service/` package)
Test the business logic directly, with all external dependencies mocked.

```
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock UserRepository userRepository;  ← fake
    @InjectMocks AuthService authService; ← real, receives the fakes
}
```

### 2. Controller Integration Tests (in `controller/` package)
Test the full HTTP layer — routing, validation, JSON, status codes, exception handling.

```java
// Spring Boot 4: use standaloneSetup (no @WebMvcTest — it was removed in SB4)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Mock AuthService authService;          // fake service
    @InjectMocks AuthController controller; // real controller

    MockMvc mockMvc;

    @BeforeEach void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler()) // exception → status mapping
            .build();
    }
}
```

---

## The AAA Pattern — every test follows this

```java
@Test
void someTest() {
    // ARRANGE — set up your fakes and inputs
    when(repository.findById(1L)).thenReturn(Optional.of(someEntity));

    // ACT — call the method you're testing
    var result = service.doSomething(1L);

    // ASSERT — verify the result is what you expected
    assertThat(result.title()).isEqualTo("Expected title");
}
```

---

## The Mockito vocabulary

```java
@Mock UserRepository userRepository;       // creates a FAKE UserRepository
@InjectMocks AuthService authService;      // creates REAL AuthService, injects the fakes

when(repo.findById(1L)).thenReturn(...);   // "when this is called, return this"
verify(repo).save(any());                  // "confirm save() was called"
verify(repo, never()).delete(any());       // "confirm delete() was NEVER called"
any()                                      // matches any argument
any(User.class)                            // matches any User argument
```

---

## The MockMvc vocabulary

```java
mockMvc.perform(post("/api/auth/register")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(body)))
    .andExpect(status().isCreated())              // verify 201 status
    .andExpect(jsonPath("$.token").exists());     // verify JSON field exists

// jsonPath cheat sheet:
//   $.token           → top-level "token" field
//   $.content[0].id   → first item in "content" array, "id" field
//   $.content.length() → length of "content" array
```

---

## How to run the tests

```bash
./mvnw test
```

All tests should be GREEN. If one is red, the output tells you exactly what went wrong.


## What you will learn

- **Unit tests vs Integration tests** — what's the difference and when to use each
- **JUnit 5** — the testing framework built into Spring Boot
- **Mockito** — creating fake (mock) objects to isolate the class you're testing
- **`@Mock` and `@InjectMocks`** — Mockito annotations
- **`when().thenReturn()`** — telling a mock what to return
- **`verify()`** — checking a mock method was actually called
- **The AAA pattern** — Arrange, Act, Assert
- **`assertThat()`** — AssertJ assertions (more readable than JUnit's `assertEquals`)
- **`assertThatThrownBy()`** — testing that exceptions are thrown
- **`@BeforeEach` / `@AfterEach`** — setup and teardown before/after each test
- **Testing security** — simulating a logged-in user in unit tests

---

## The Core Idea — Why test?

> "Code that isn't tested is code that doesn't work — you just don't know it yet."

Tests give you confidence to change code. Without tests, every change risks
breaking something silently. With tests, you get immediate red/green feedback.

---

## Unit Test vs Integration Test

| | Unit Test | Integration Test |
|---|---|---|
| What | Tests ONE class in isolation | Tests multiple classes working together |
| Database needed? | ❌ No | ✅ Yes (or H2) |
| Speed | ⚡ Milliseconds | 🐢 Seconds |
| How | Mock all dependencies | Use real objects |
| When | Always, for every method | For critical flows |

**This project uses unit tests.** They test the service logic with mocked repositories.

---

## The AAA Pattern — every test follows this

```java
@Test
void someTest() {
    // ARRANGE — set up your fakes and inputs
    when(repository.findById(1L)).thenReturn(Optional.of(someEntity));

    // ACT — call the method you're testing
    var result = service.doSomething(1L);

    // ASSERT — verify the result is what you expected
    assertThat(result.title()).isEqualTo("Expected title");
}
```

---

## The Mockito vocabulary

```java
@Mock UserRepository userRepository;       // creates a FAKE UserRepository
@InjectMocks AuthService authService;      // creates REAL AuthService, injects the fakes

when(repo.findById(1L)).thenReturn(...);   // "when this is called, return this"
verify(repo).save(any());                  // "confirm save() was called"
verify(repo, never()).delete(any());       // "confirm delete() was NEVER called"
any()                                      // matches any argument
any(User.class)                            // matches any User argument
```

---

## How to run the tests

```bash
./mvnw test
```

All tests should be GREEN. If one is red, the output tells you exactly what went wrong.

