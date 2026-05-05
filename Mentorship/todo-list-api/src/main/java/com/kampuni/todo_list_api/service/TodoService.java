package com.kampuni.todo_list_api.service;

import com.kampuni.todo_list_api.dto.TodoRequest;
import com.kampuni.todo_list_api.dto.TodoResponse;
import com.kampuni.todo_list_api.entity.ToDo;
import com.kampuni.todo_list_api.entity.User;
import com.kampuni.todo_list_api.repository.TodoRepository;
import com.kampuni.todo_list_api.repository.UserRepository;
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

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/v1/todos  →  return all todos for the logged-in user
    // ─────────────────────────────────────────────────────────────────────────
    public List<TodoResponse> getMyTodos() {
        User user = getLoggedInUser();
        // TodoRepository.findByUserId() uses the user.id to filter rows in the todos table.
        return todoRepository.findByUserId(user.getId())
                .stream()
                // ✅ BEST PRACTICE: Convert each entity to a DTO before returning.
                //    This keeps your internal data model separate from what the API exposes.
                .map(this::toResponse)
                .toList();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/v1/todos  →  create a new todo for the logged-in user
    // ─────────────────────────────────────────────────────────────────────────
    public TodoResponse createTodo(TodoRequest request) {
        User user = getLoggedInUser();

        ToDo todo = new ToDo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(false);   // new todos always start as not completed
        todo.setUser(user);         // ✅ link this todo to the logged-in user

        ToDo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/v1/todos/{id}  →  update title/description of an existing todo
    // ─────────────────────────────────────────────────────────────────────────
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        ToDo todo = findTodoForCurrentUser(id);

        todo.setTitle(request.title());
        todo.setDescription(request.description());

        ToDo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PATCH /api/v1/todos/{id}/complete  →  mark a todo as completed (or toggle)
    // ─────────────────────────────────────────────────────────────────────────
    // ✅ BEST PRACTICE: Use PATCH (not PUT) for a partial update — changing only one field.
    //    PUT means "replace the whole resource". PATCH means "change part of it".
    public TodoResponse completeTodo(Long id) {
        ToDo todo = findTodoForCurrentUser(id);
        todo.setCompleted(!todo.isCompleted()); // toggle: true→false, false→true
        return toResponse(todoRepository.save(todo));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/v1/todos/{id}  →  delete a todo
    // ─────────────────────────────────────────────────────────────────────────
    public void deleteTodo(Long id) {
        ToDo todo = findTodoForCurrentUser(id);
        todoRepository.delete(todo);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Reads the email from the JWT token (already placed in SecurityContext by JwtAuthFilter)
     * and loads the User from the database.
     *
     * ✅ SECURITY: We ALWAYS look up the user from the token — never trust a userId
     *    sent in the request body, because a user could fake it to access another user's data.
     */
    private User getLoggedInUser() {
        // The JwtAuthFilter already put the authenticated email here — we just read it.
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Finds a todo by id AND verifies it belongs to the currently logged-in user.
     *
     * ✅ SECURITY: This is called an "ownership check" (or authorization check).
     *    Without it, user A could delete user B's todos just by knowing the ID.
     *    This is called an IDOR vulnerability (Insecure Direct Object Reference).
     */
    private ToDo findTodoForCurrentUser(Long id) {
        User user = getLoggedInUser();
        ToDo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        // Check: does this todo actually belong to the logged-in user?
        if (!todo.getUser().getId().equals(user.getId())) {
            // ✅ SECURITY: Return "not found" instead of "forbidden" so we don't reveal
            //    that a todo with this ID exists — it's none of their business!
            throw new RuntimeException("Todo not found");
        }
        return todo;
    }

    /**
     * Converts a ToDo entity into a TodoResponse DTO.
     * ✅ BEST PRACTICE: Keep mapping in one place so you only update it once.
     */
    private TodoResponse toResponse(ToDo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt()
        );
    }
}

