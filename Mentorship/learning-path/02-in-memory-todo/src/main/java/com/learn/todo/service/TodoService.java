package com.learn.todo.service;

import com.learn.todo.dto.TodoRequest;
import com.learn.todo.dto.TodoResponse;
import com.learn.todo.model.Todo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: The Service Layer
//
// @Service marks this class as a "service" — Spring will:
//   1. Create exactly ONE instance of it when the app starts (a "singleton bean")
//   2. Make it available for injection into other classes (like the controller)
//
// The service contains ALL of the business logic.
// The controller just delegates to the service.
//
// Rule: If you're tempted to put logic in the controller, move it to the service.
// ══════════════════════════════════════════════════════════════════════════════
@Service
public class TodoService {

    // Our "database" for now — just a list in memory.
    // Warning: this resets every time the server restarts. That's fine for learning.
    // In Project 3 we replace this with a real database.
    private final List<Todo> todos = new ArrayList<>();

    // AtomicLong generates unique IDs safely even with concurrent requests.
    private final AtomicLong idCounter = new AtomicLong(1);

    // ──────────────────────────────────────────────────────────────────────────
    // GET all todos
    // ──────────────────────────────────────────────────────────────────────────
    public List<TodoResponse> getAll() {
        // .stream()        → treat the list as a pipeline
        // .map(toResponse) → convert each Todo model into a TodoResponse DTO
        // .toList()        → collect the results back into a list
        return todos.stream()
                .map(this::toResponse)
                .toList();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GET one todo by ID
    // ──────────────────────────────────────────────────────────────────────────
    public Optional<TodoResponse> getById(Long id) {
        // Optional<T> means "this might or might not have a value".
        // The controller decides what to do when it's empty (return 404).
        return todos.stream()
                .filter(t -> t.getId().equals(id))
                .map(this::toResponse)
                .findFirst();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // CREATE a new todo
    // ──────────────────────────────────────────────────────────────────────────
    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo(idCounter.getAndIncrement(), request.title(), request.description());
        todos.add(todo);
        return toResponse(todo);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // UPDATE an existing todo
    // ──────────────────────────────────────────────────────────────────────────
    public Optional<TodoResponse> update(Long id, TodoRequest request) {
        return todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .map(todo -> {
                    todo.setTitle(request.title());
                    todo.setDescription(request.description());
                    return toResponse(todo);
                });
        // If the id doesn't exist, this returns Optional.empty()
        // The controller will turn that into a 404 response
    }

    // ──────────────────────────────────────────────────────────────────────────
    // DELETE a todo
    // ──────────────────────────────────────────────────────────────────────────
    public boolean delete(Long id) {
        // removeIf returns true if something was removed
        return todos.removeIf(t -> t.getId().equals(id));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TOGGLE completed status
    // ──────────────────────────────────────────────────────────────────────────
    public Optional<TodoResponse> toggleComplete(Long id) {
        return todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .map(todo -> {
                    todo.setCompleted(!todo.isCompleted()); // flip true↔false
                    return toResponse(todo);
                });
    }

    // ──────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPER: converts internal model → response DTO
    // ──────────────────────────────────────────────────────────────────────────
    // Keeping this in one place means you only update the mapping in ONE spot.
    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt()
        );
    }
}

