package com.learn.secured.controller;

import com.learn.secured.dto.TodoRequest;
import com.learn.secured.dto.TodoResponse;
import com.learn.secured.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;
    public TodoController(TodoService todoService) { this.todoService = todoService; }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getMyTodos() {
        return ResponseEntity.ok(todoService.getMyTodos());
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createTodo(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        return ResponseEntity.ok(todoService.updateTodo(id, request));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TodoResponse> completeTodo(@PathVariable Long id) {
        return ResponseEntity.ok(todoService.completeTodo(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}

