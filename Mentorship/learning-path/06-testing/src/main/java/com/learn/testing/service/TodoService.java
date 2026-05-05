package com.learn.testing.service;
import com.learn.testing.dto.*;
import com.learn.testing.entity.Todo;
import com.learn.testing.entity.User;
import com.learn.testing.repository.TodoRepository;
import com.learn.testing.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    public TodoService(TodoRepository t, UserRepository u) { todoRepository=t; userRepository=u; }
    public List<TodoResponse> getMyTodos() {
        return todoRepository.findByUserId(getLoggedInUser().getId()).stream().map(this::toResponse).toList();
    }
    public TodoResponse createTodo(TodoRequest req) {
        Todo todo = new Todo(); todo.setTitle(req.title()); todo.setDescription(req.description());
        todo.setUser(getLoggedInUser()); return toResponse(todoRepository.save(todo));
    }
    public TodoResponse updateTodo(Long id, TodoRequest req) {
        Todo todo = findOwnedTodo(id); todo.setTitle(req.title()); todo.setDescription(req.description());
        return toResponse(todoRepository.save(todo));
    }
    public TodoResponse completeTodo(Long id) {
        Todo todo = findOwnedTodo(id); todo.setCompleted(!todo.isCompleted());
        return toResponse(todoRepository.save(todo));
    }
    public void deleteTodo(Long id) { todoRepository.delete(findOwnedTodo(id)); }
    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
    private Todo findOwnedTodo(Long id) {
        User user = getLoggedInUser();
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Todo not found"));
        if (!todo.getUser().getId().equals(user.getId())) throw new RuntimeException("Todo not found");
        return todo;
    }
    private TodoResponse toResponse(Todo t) {
        return new TodoResponse(t.getId(), t.getTitle(), t.getDescription(), t.isCompleted(), t.getCreatedAt());
    }
}

