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

