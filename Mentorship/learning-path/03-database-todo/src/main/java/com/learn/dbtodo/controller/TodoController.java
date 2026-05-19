package com.learn.dbtodo.controller;

import com.learn.dbtodo.dto.TodoRequest;
import com.learn.dbtodo.dto.TodoResponse;
import com.learn.dbtodo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// This controller is almost identical to Project 02's controller.
// The only change: getAll() now accepts a Pageable parameter.
// The controller knows NOTHING about the database change — it just talks to TodoService.
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: Pageable in the controller
    //
    // Spring MVC automatically reads these query parameters and builds a Pageable:
    //   GET /api/todos                          → page 0, 10 items, sorted by createdAt DESC
    //   GET /api/todos?page=1                   → page 1, 10 items
    //   GET /api/todos?page=0&size=5            → page 0, 5 items
    //   GET /api/todos?sort=title,asc           → sorted by title ascending
    //
    // @PageableDefault sets the DEFAULT values used when the client sends NO params.
    // Without it, Spring defaults to page=0, size=20, unsorted.
    // ══════════════════════════════════════════════════════════════════════════
    @GetMapping
    public ResponseEntity<Page<TodoResponse>> getAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(todoService.getAll(pageable));
        // The response body is now a Page<TodoResponse>, which looks like:
        // {
        //   "content": [ {...}, {...} ],
        //   "totalElements": 42,
        //   "totalPages": 5,
        //   "number": 0,
        //   "size": 10
        // }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getById(@PathVariable Long id) {
        return todoService.getById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody TodoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        return todoService.update(id, request).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoResponse> toggleComplete(@PathVariable Long id) {
        return todoService.toggleComplete(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return todoService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
