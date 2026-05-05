package com.learn.dbtodo.controller;

import com.learn.dbtodo.dto.TodoRequest;
import com.learn.dbtodo.dto.TodoResponse;
import com.learn.dbtodo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// This controller is IDENTICAL to Project 02's controller.
// The controller knows NOTHING about the database change — it just talks to TodoService.
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAll() {
        return ResponseEntity.ok(todoService.getAll());
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

