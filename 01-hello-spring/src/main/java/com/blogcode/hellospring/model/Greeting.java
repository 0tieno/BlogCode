package com.blogcode.hellospring.model;

import java.time.Instant;

/**
 * Immutable domain object representing a single greeting message returned to
 * clients of this API.
 *
 * <p>This is implemented as a Java {@code record} rather than a classic
 * class with private fields, getters, a constructor and
 * {@code equals}/{@code hashCode}/{@code toString}. Records were introduced
 * to eliminate exactly that kind of boilerplate: the compiler generates all
 * of it for us from the component list declared in the header below. This
 * makes {@code Greeting} a perfect teaching example of "value objects" -
 * simple, immutable data carriers with no behaviour of their own.
 *
 * <p>Because instances are immutable (every field is {@code final} under the
 * hood), a {@code Greeting} can be safely shared between threads and passed
 * around without fear of it being mutated unexpectedly - an important
 * property for objects that flow through a web layer as JSON responses.
 *
 * @param id        a simple numeric identifier for the greeting, useful once
 *                  students later learn about persisting entities (see the
 *                  02-student-crud-api project for a database-backed
 *                  example).
 * @param message   the actual greeting text shown to the caller, e.g.
 *                  {@code "Hello, World!"}.
 * @param createdAt the server-side timestamp at which the greeting was
 *                  generated; demonstrates returning non-trivial types
 *                  (here {@link Instant}) as part of a JSON response, which
 *                  Spring's Jackson integration serializes automatically.
 */
public record Greeting(Long id, String message, Instant createdAt) {
}
