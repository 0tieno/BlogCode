package com.learn.dbtodo.service;

import com.learn.dbtodo.dto.TodoRequest;
import com.learn.dbtodo.dto.TodoResponse;
import com.learn.dbtodo.entity.Todo;
import com.learn.dbtodo.exception.TodoNotFoundException;
import com.learn.dbtodo.repository.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: @Slf4j — Logging with Lombok
//
// @Slf4j on a class generates: private static final Logger log = ...
// You then call log.info(), log.warn(), log.error() in your methods.
//
// See it in action below — every important operation is logged.
// ══════════════════════════════════════════════════════════════════════════════
@Slf4j
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT: Pagination
    //
    // PROBLEM: todoRepository.findAll() returns ALL todos in one go.
    //   If there are 1,000,000 rows, you just loaded 1 million objects into
    //   memory. Your server runs out of RAM and crashes. Never do this in production.
    //
    // SOLUTION: Pagination — ask for ONE PAGE of results at a time.
    //
    // Spring's Pageable interface carries three things from the client:
    //   page  = which page to return (0-indexed, so page 0 = first page)
    //   size  = how many items per page
    //   sort  = how to order results (e.g., "createdAt,desc")
    //
    // The client calls: GET /api/todos?page=0&size=10&sort=createdAt,desc
    //
    // Page<TodoResponse> wraps the results AND includes metadata:
    //   content         → the actual list of todos for this page
    //   totalElements   → total todos in the database
    //   totalPages      → how many pages there are
    //   number          → current page number
    //   size            → items per page
    //
    // Spring passes Pageable to the repository automatically — JpaRepository
    // already has findAll(Pageable pageable) built in. No custom SQL needed.
    // ══════════════════════════════════════════════════════════════════════════
    public Page<TodoResponse> getAll(Pageable pageable) {
        log.debug("Fetching todos — page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return todoRepository.findAll(pageable).map(this::toResponse);
        //                               ↑              ↑
        //   Spring queries DB for this page   Page has .map() just like Stream
    }

    public Optional<TodoResponse> getById(Long id) {
        log.debug("Fetching todo by id: {}", id);
        return todoRepository.findById(id).map(this::toResponse);
    }

    // ── @Transactional on every WRITE operation ────────────────────────────
    // If the save() call throws (e.g., DB constraint violated), the whole
    // method is rolled back — no partial data ends up in the database.
    @Transactional
    public TodoResponse create(TodoRequest request) {
        log.info("Creating todo: '{}'", request.title());
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        Todo saved = todoRepository.save(todo);
        log.info("Todo created with id: {}", saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public Optional<TodoResponse> update(Long id, TodoRequest request) {
        log.info("Updating todo id: {}", id);
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setTitle(request.title());
                    todo.setDescription(request.description());
                    return toResponse(todoRepository.save(todo));
                });
    }

    @Transactional
    public Optional<TodoResponse> toggleComplete(Long id) {
        log.info("Toggling complete for todo id: {}", id);
        return todoRepository.findById(id)
                .map(todo -> {
                    todo.setCompleted(!todo.isCompleted());
                    return toResponse(todoRepository.save(todo));
                });
    }

    @Transactional
    public boolean delete(Long id) {
        log.info("Deleting todo id: {}", id);
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.getDescription(),
                todo.isCompleted(), todo.getCreatedAt());
    }
}
