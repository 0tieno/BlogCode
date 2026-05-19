package com.learn.secured.exception;

// ══════════════════════════════════════════════════════════════════════════════
// HTTP 404 Not Found — the requested todo doesn't exist (or doesn't belong
// to the logged-in user — we return the same status deliberately, see
// TodoService.findOwnedTodo() for the IDOR-prevention reasoning).
// ══════════════════════════════════════════════════════════════════════════════
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("Todo not found: " + id);
    }
}

