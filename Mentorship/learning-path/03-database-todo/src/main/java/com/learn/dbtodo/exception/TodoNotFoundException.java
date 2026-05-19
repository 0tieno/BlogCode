package com.learn.dbtodo.exception;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Custom Exception → Correct HTTP Status Code
//
// When a todo is not found, throwing TodoNotFoundException automatically
// produces a 404 Not Found response (via the GlobalExceptionHandler).
//
// Compare to Project 02 where missing todos returned Optional.empty() and
// the controller returned 404 manually. Both patterns are valid:
//
//   Pattern A (Project 02): service returns Optional → controller decides status
//   Pattern B (Project 03+): service throws exception → handler decides status
//
// Pattern B is preferred in larger apps because:
//   - Deeply nested service calls can throw without threading Optional<> through
//     every intermediate method.
//   - Error handling lives in ONE place (GlobalExceptionHandler), not in
//     every controller method.
// ══════════════════════════════════════════════════════════════════════════════
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("Todo not found: " + id);
    }
}

