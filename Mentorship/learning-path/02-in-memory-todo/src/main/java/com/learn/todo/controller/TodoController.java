package com.learn.todo.controller;

import com.learn.todo.dto.TodoRequest;
import com.learn.todo.dto.TodoResponse;
import com.learn.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Dependency Injection
//
// TodoController NEEDS a TodoService to work.
// It doesn't create the service itself — Spring does, and "injects" it.
//
// WHY? Because:
//   1. You don't control the lifecycle — Spring does (singleton, prototype, etc.)
//   2. It makes testing easy — you can inject a FAKE service in tests
//   3. Your classes are loosely coupled — TodoController doesn't know HOW TodoService works
//
// CONSTRUCTOR INJECTION (recommended):
//   You declare the dependency as a final field.
//   Spring sees the constructor and automatically injects the matching bean.
//   IntelliJ will tell you "Constructor injection is preferred over @Autowired field injection".
// ══════════════════════════════════════════════════════════════════════════════
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    // final = once set in the constructor, it can never be changed. This is a good practice.
    private final TodoService todoService;

    // Spring calls this constructor when creating TodoController,
    // and automatically provides the TodoService bean.
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // GET /api/todos → returns all todos
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAll() {
        return ResponseEntity.ok(todoService.getAll());
    }

    // GET /api/todos/{id} → returns one todo, or 404 if not found
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getById(@PathVariable Long id) {
        // .map(ResponseEntity::ok) → if present, wrap in 200 OK
        // .orElse(notFound)        → if empty, return 404 Not Found
        return todoService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/todos → creates a new todo
    // @Valid activates the validation rules defined in TodoRequest (@NotBlank etc.)
    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody TodoRequest request) {
        TodoResponse created = todoService.create(request);
        // 201 Created — the correct status for a successful resource creation
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT /api/todos/{id} → updates a todo, or 404 if not found
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request
    ) {
        return todoService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH /api/todos/{id}/complete → toggles completed status
    // PATCH = partial update (one field), PUT = full replacement
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoResponse> toggleComplete(@PathVariable Long id) {
        return todoService.toggleComplete(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/todos/{id} → deletes a todo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (todoService.delete(id)) {
            // 204 No Content — success, but nothing to return
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

