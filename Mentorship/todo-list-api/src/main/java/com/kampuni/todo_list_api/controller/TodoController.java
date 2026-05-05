package com.kampuni.todo_list_api.controller;

import com.kampuni.todo_list_api.dto.TodoRequest;
import com.kampuni.todo_list_api.dto.TodoResponse;
import com.kampuni.todo_list_api.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ✅ BEST PRACTICE: Each resource gets its own controller.
//    Auth lives in AuthController, todos live here in TodoController.
//    This keeps each file small and focused (Single Responsibility Principle).
@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/v1/todos
    // Returns all todos for the currently logged-in user.
    // The controller does NOT need to receive the user — the service reads it
    // from the JWT token via SecurityContextHolder. Clean separation!
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getMyTodos() {
        return ResponseEntity.ok(todoService.getMyTodos());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/v1/todos
    // Creates a new todo. Returns 201 CREATED with the new todo in the body.
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            @Valid @RequestBody TodoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createTodo(request));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/v1/todos/{id}
    // Updates title and/or description of an existing todo.
    // {id} in the URL is called a "path variable".
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request
    ) {
        return ResponseEntity.ok(todoService.updateTodo(id, request));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /api/v1/todos/{id}/complete
    // Toggles the completed status (true → false, false → true).
    // ✅ BEST PRACTICE: Use PATCH for partial updates. No request body needed here
    //    because we are just toggling a single field.
    // ─────────────────────────────────────────────────────────────────────────
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoResponse> completeTodo(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.completeTodo(id));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/v1/todos/{id}
    // Deletes a todo. Returns 204 NO CONTENT — success, but nothing to return.
    // ✅ BEST PRACTICE: Use 204 (not 200) for DELETE. There is no response body.
    // ─────────────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}

