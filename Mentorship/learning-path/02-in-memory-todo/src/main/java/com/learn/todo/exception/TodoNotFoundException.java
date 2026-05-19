package com.learn.todo.exception;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Custom Exceptions
//
// Instead of throwing new RuntimeException("Todo not found"), we create a
// dedicated exception class. This has three benefits:
//
//   1. The GlobalExceptionHandler can catch THIS type specifically and
//      return the CORRECT HTTP status code (404, not the generic 400).
//
//   2. The code is self-documenting: "TodoNotFoundException" is instantly
//      clear. "RuntimeException" tells you nothing.
//
//   3. You can add fields (like the missing id) for richer error messages
//      without string-juggling everywhere exceptions are thrown.
//
// We extend RuntimeException (not Exception) because:
//   - Checked exceptions (extends Exception) force every caller to write
//     try/catch blocks — ugly and unnecessary for "business rule" errors.
//   - Unchecked RuntimeExceptions propagate automatically to the
//     GlobalExceptionHandler, which catches and formats them.
// ══════════════════════════════════════════════════════════════════════════════
public class TodoNotFoundException extends RuntimeException {

    // The id is stored so the error message is specific: "Todo not found: 42"
    // rather than just "Todo not found" — much easier to debug.
    public TodoNotFoundException(Long id) {
        super("Todo not found: " + id);
    }
}

