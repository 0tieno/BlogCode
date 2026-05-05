package com.learn.dbtodo.service;

import com.learn.dbtodo.dto.TodoRequest;
import com.learn.dbtodo.dto.TodoResponse;
import com.learn.dbtodo.entity.Todo;
import com.learn.dbtodo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// ══════════════════════════════════════════════════════════════════════════════
// Notice: compared to Project 02, the service logic is almost identical!
// The ONLY difference is that instead of a List<Todo> in memory,
// we now use todoRepository which talks to the H2 database.
//
// This is the power of good layer separation:
//   - The Controller didn't change at all
//   - The Service barely changed
//   - Only the storage backing changed
// ══════════════════════════════════════════════════════════════════════════════
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoResponse> getAll() {
        return todoRepository.findAll()    // ← was: todos (a List)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<TodoResponse> getById(Long id) {
        return todoRepository.findById(id) // ← was: todos.stream().filter(...)
                .map(this::toResponse);
    }

    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        // createdAt is set automatically by @PrePersist
        // completed defaults to false

        Todo saved = todoRepository.save(todo); // ← was: todos.add(todo)
        return toResponse(saved);
    }

    public Optional<TodoResponse> update(Long id, TodoRequest request) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setTitle(request.title());
                    todo.setDescription(request.description());
                    return toResponse(todoRepository.save(todo)); // save() does UPDATE if id exists
                });
    }

    public Optional<TodoResponse> toggleComplete(Long id) {
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setCompleted(!todo.isCompleted());
                    return toResponse(todoRepository.save(todo));
                });
    }

    public boolean delete(Long id) {
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id); // ← was: todos.removeIf(...)
            return true;
        }
        return false;
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.getDescription(),
                todo.isCompleted(), todo.getCreatedAt());
    }
}

