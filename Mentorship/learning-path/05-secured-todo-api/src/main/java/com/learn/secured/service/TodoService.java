package com.learn.secured.service;

import com.learn.secured.dto.TodoRequest;
import com.learn.secured.dto.TodoResponse;
import com.learn.secured.entity.Todo;
import com.learn.secured.entity.User;
import com.learn.secured.exception.TodoNotFoundException;
import com.learn.secured.repository.TodoRepository;
import com.learn.secured.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Pagination: returns only the LOGGED-IN USER's todos, one page at a time.
    // The client calls: GET /api/todos?page=0&size=10&sort=createdAt,desc
    // ══════════════════════════════════════════════════════════════════════════
    public Page<TodoResponse> getMyTodos(Pageable pageable) {
        User user = getLoggedInUser();
        log.debug("Fetching todos for user: {} — page: {}", user.getEmail(), pageable.getPageNumber());
        return todoRepository.findByUserId(user.getId(), pageable).map(this::toResponse);
    }

    @Transactional
    public TodoResponse createTodo(TodoRequest request) {
        User user = getLoggedInUser();
        log.info("Creating todo '{}' for user: {}", request.title(), user.getEmail());
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setUser(user);
        return toResponse(todoRepository.save(todo));
    }

    @Transactional
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        log.info("Updating todo id: {}", id);
        Todo todo = findOwnedTodo(id);
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        return toResponse(todoRepository.save(todo));
    }

    @Transactional
    public TodoResponse completeTodo(Long id) {
        log.info("Toggling complete for todo id: {}", id);
        Todo todo = findOwnedTodo(id);
        todo.setCompleted(!todo.isCompleted());
        return toResponse(todoRepository.save(todo));
    }

    @Transactional
    public void deleteTodo(Long id) {
        log.info("Deleting todo id: {}", id);
        todoRepository.delete(findOwnedTodo(id));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // KEY CONCEPT: Reading the logged-in user from the SecurityContext
    // JwtAuthFilter ran before this. We just READ what it stored.
    // We NEVER trust a userId from the request body.
    // ══════════════════════════════════════════════════════════════════════════
    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // KEY CONCEPT: Ownership check — prevents IDOR attacks
    // Throws TodoNotFoundException (not "Access denied") so the attacker cannot
    // even confirm whether the id exists. Same status = no information leak.
    // ══════════════════════════════════════════════════════════════════════════
    private Todo findOwnedTodo(Long id) {
        User user = getLoggedInUser();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        if (!todo.getUser().getId().equals(user.getId())) {
            log.warn("IDOR attempt: user {} tried to access todo {} owned by someone else",
                    user.getEmail(), id);
            throw new TodoNotFoundException(id); // same message — intentional
        }
        return todo;
    }

    private TodoResponse toResponse(Todo t) {
        return new TodoResponse(t.getId(), t.getTitle(), t.getDescription(), t.isCompleted(), t.getCreatedAt());
    }
}
