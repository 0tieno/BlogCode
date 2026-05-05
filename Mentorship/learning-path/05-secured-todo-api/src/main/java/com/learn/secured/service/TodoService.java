package com.learn.secured.service;

import com.learn.secured.dto.TodoRequest;
import com.learn.secured.dto.TodoResponse;
import com.learn.secured.entity.Todo;
import com.learn.secured.entity.User;
import com.learn.secured.repository.TodoRepository;
import com.learn.secured.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public List<TodoResponse> getMyTodos() {
        return todoRepository.findByUserId(getLoggedInUser().getId())
                .stream().map(this::toResponse).toList();
    }

    public TodoResponse createTodo(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setUser(getLoggedInUser()); // link to the logged-in user
        return toResponse(todoRepository.save(todo));
    }

    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todo = findOwnedTodo(id);
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        return toResponse(todoRepository.save(todo));
    }

    public TodoResponse completeTodo(Long id) {
        Todo todo = findOwnedTodo(id);
        todo.setCompleted(!todo.isCompleted());
        return toResponse(todoRepository.save(todo));
    }

    public void deleteTodo(Long id) {
        todoRepository.delete(findOwnedTodo(id));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // KEY CONCEPT: Reading the logged-in user from the SecurityContext
    //
    // JwtAuthFilter ran before this method. It validated the token and called:
    //   SecurityContextHolder.getContext().setAuthentication(authToken)
    //
    // Now we just READ that email back. We then load the full User from the DB.
    // We NEVER trust a userId sent in the request body.
    // ══════════════════════════════════════════════════════════════════════════
    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // KEY CONCEPT: Ownership check — prevents IDOR attacks
    //
    // Without this, user A could update/delete user B's todos just by knowing the ID.
    // We verify: "does this todo belong to the logged-in user?"
    // If not, we throw "Todo not found" — deliberately vague so the attacker
    // can't even tell whether the ID exists.
    // ══════════════════════════════════════════════════════════════════════════
    private Todo findOwnedTodo(Long id) {
        User user = getLoggedInUser();
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Todo not found"); // same message as above — intentional
        }
        return todo;
    }

    private TodoResponse toResponse(Todo t) {
        return new TodoResponse(t.getId(), t.getTitle(), t.getDescription(), t.isCompleted(), t.getCreatedAt());
    }
}

