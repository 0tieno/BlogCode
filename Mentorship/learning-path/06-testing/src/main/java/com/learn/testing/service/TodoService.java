package com.learn.testing.service;

import com.learn.testing.dto.TodoRequest;
import com.learn.testing.dto.TodoResponse;
import com.learn.testing.entity.Todo;
import com.learn.testing.entity.User;
import com.learn.testing.exception.TodoNotFoundException;
import com.learn.testing.repository.TodoRepository;
import com.learn.testing.repository.UserRepository;
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

    public TodoService(TodoRepository t, UserRepository u) {
        todoRepository = t;
        userRepository = u;
    }

    public Page<TodoResponse> getMyTodos(Pageable pageable) {
        return todoRepository.findByUserId(getLoggedInUser().getId(), pageable)
                .map(this::toResponse);
    }

    @Transactional
    public TodoResponse createTodo(TodoRequest req) {
        Todo todo = new Todo();
        todo.setTitle(req.title());
        todo.setDescription(req.description());
        todo.setUser(getLoggedInUser());
        return toResponse(todoRepository.save(todo));
    }

    @Transactional
    public TodoResponse updateTodo(Long id, TodoRequest req) {
        Todo todo = findOwnedTodo(id);
        todo.setTitle(req.title());
        todo.setDescription(req.description());
        return toResponse(todoRepository.save(todo));
    }

    @Transactional
    public TodoResponse completeTodo(Long id) {
        Todo todo = findOwnedTodo(id);
        todo.setCompleted(!todo.isCompleted());
        return toResponse(todoRepository.save(todo));
    }

    @Transactional
    public void deleteTodo(Long id) {
        todoRepository.delete(findOwnedTodo(id));
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Todo findOwnedTodo(Long id) {
        User user = getLoggedInUser();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        if (!todo.getUser().getId().equals(user.getId())) {
            log.warn("IDOR attempt: user {} tried to access todo {}", user.getEmail(), id);
            throw new TodoNotFoundException(id);
        }
        return todo;
    }

    private TodoResponse toResponse(Todo t) {
        return new TodoResponse(t.getId(), t.getTitle(), t.getDescription(), t.isCompleted(), t.getCreatedAt());
    }
}
