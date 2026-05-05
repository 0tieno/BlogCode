package com.kampuni.todo_list_api.service;

import com.kampuni.todo_list_api.dto.TodoRequest;
import com.kampuni.todo_list_api.dto.TodoResponse;
import com.kampuni.todo_list_api.entity.ToDo;
import com.kampuni.todo_list_api.entity.User;
import com.kampuni.todo_list_api.repository.TodoRepository;
import com.kampuni.todo_list_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;

    // ─────────────────────────────────────────────────────────────────────────
    // Test data — shared across all tests
    // ─────────────────────────────────────────────────────────────────────────
    private User alice;
    private User bob;

    // ✅ @BeforeEach runs before EVERY test method.
    //    Here we do two things:
    //    1. Create test users
    //    2. Simulate a logged-in user (alice) in Spring Security's context
    @BeforeEach
    void setUp() {
        alice = buildUser(1L, "alice@test.com");
        bob   = buildUser(2L, "bob@test.com");

        // ✅ CONCEPT: Simulating a logged-in user in unit tests.
        //    In real requests, JwtAuthFilter puts the user's email into SecurityContextHolder.
        //    In unit tests (no HTTP, no filter), we put it there manually.
        //    UsernamePasswordAuthenticationToken is the standard Spring Security way to do this.
        var auth = new UsernamePasswordAuthenticationToken("alice@test.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ✅ @AfterEach runs after EVERY test method.
    //    Always clear the SecurityContext so one test doesn't affect the next.
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // getMyTodos()
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void getMyTodos_returnsOnlyLoggedInUsersItems() {
        // ARRANGE — alice has 2 todos
        List<ToDo> aliceTodos = List.of(
                buildTodo(1L, alice, false),
                buildTodo(2L, alice, true)
        );
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findByUserId(1L)).thenReturn(aliceTodos);

        // ACT
        List<TodoResponse> result = todoService.getMyTodos();

        // ASSERT
        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("Todo 1");
        assertThat(result.get(1).completed()).isTrue();

        // ✅ Confirm the query was made using alice's ID (1L), not bob's (2L)
        verify(todoRepository).findByUserId(1L);
    }

    @Test
    void getMyTodos_noTodos_returnsEmptyList() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findByUserId(1L)).thenReturn(List.of());

        List<TodoResponse> result = todoService.getMyTodos();

        assertThat(result).isEmpty();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // createTodo()
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void createTodo_savesAndReturnsMappedResponse() {
        // ARRANGE
        TodoRequest request = new TodoRequest("Buy milk", "From corner shop");
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));

        // Simulate what save() does: assign an ID and return the entity
        when(todoRepository.save(any(ToDo.class))).thenAnswer(invocation -> {
            ToDo todo = invocation.getArgument(0);
            todo.setId(99L);
            // @PrePersist doesn't run in unit tests (no JPA lifecycle), so set it manually
            todo.setCreatedAt(LocalDateTime.now());
            return todo;
        });

        // ACT
        TodoResponse response = todoService.createTodo(request);

        // ASSERT
        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.title()).isEqualTo("Buy milk");
        assertThat(response.description()).isEqualTo("From corner shop");
        assertThat(response.completed()).isFalse(); // new todos are never completed
        assertThat(response.createdAt()).isNotNull();

        verify(todoRepository).save(any(ToDo.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // updateTodo()
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void updateTodo_ownedByUser_updatesFieldsAndReturns() {
        // ARRANGE
        ToDo existingTodo = buildTodo(10L, alice, false);
        TodoRequest updateRequest = new TodoRequest("Updated title", "Updated description");

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(10L)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(ToDo.class))).thenAnswer(i -> i.getArgument(0));

        // ACT
        TodoResponse response = todoService.updateTodo(10L, updateRequest);

        // ASSERT
        assertThat(response.title()).isEqualTo("Updated title");
        assertThat(response.description()).isEqualTo("Updated description");
    }

    @Test
    void updateTodo_belongsToAnotherUser_throwsNotFound() {
        // ARRANGE — the todo belongs to BOB, but ALICE is logged in
        ToDo bobsTodo = buildTodo(10L, bob, false);

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(10L)).thenReturn(Optional.of(bobsTodo));

        // ACT & ASSERT
        // ✅ SECURITY: Alice must NOT be able to update Bob's todos.
        //    The service should throw an exception and never reach save().
        assertThatThrownBy(() -> todoService.updateTodo(10L, new TodoRequest("Hack", null)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Todo not found"); // vague on purpose (IDOR prevention)

        verify(todoRepository, never()).save(any());
    }

    @Test
    void updateTodo_idDoesNotExist_throwsNotFound() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.updateTodo(999L, new TodoRequest("x", null)))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Todo not found");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // completeTodo()
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void completeTodo_notYetCompleted_togglesToTrue() {
        // ARRANGE — todo starts as NOT completed
        ToDo todo = buildTodo(5L, alice, false);

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(5L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(ToDo.class))).thenAnswer(i -> i.getArgument(0));

        // ACT
        TodoResponse response = todoService.completeTodo(5L);

        // ASSERT — should now be completed
        assertThat(response.completed()).isTrue();
    }

    @Test
    void completeTodo_alreadyCompleted_togglesBackToFalse() {
        // ARRANGE — todo starts as COMPLETED
        ToDo todo = buildTodo(5L, alice, true);

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(5L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(ToDo.class))).thenAnswer(i -> i.getArgument(0));

        // ACT
        TodoResponse response = todoService.completeTodo(5L);

        // ASSERT — should toggle back to not completed
        assertThat(response.completed()).isFalse();
    }

    @Test
    void completeTodo_belongsToAnotherUser_throwsNotFound() {
        ToDo bobsTodo = buildTodo(5L, bob, false);

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(5L)).thenReturn(Optional.of(bobsTodo));

        assertThatThrownBy(() -> todoService.completeTodo(5L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Todo not found");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // deleteTodo()
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void deleteTodo_ownedByUser_deletesSuccessfully() {
        // ARRANGE
        ToDo todo = buildTodo(7L, alice, false);

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(7L)).thenReturn(Optional.of(todo));

        // ACT — should not throw
        todoService.deleteTodo(7L);

        // ASSERT — confirm delete was called with the exact todo object
        verify(todoRepository).delete(todo);
    }

    @Test
    void deleteTodo_belongsToAnotherUser_throwsAndDoesNotDelete() {
        // ARRANGE — the todo belongs to BOB
        ToDo bobsTodo = buildTodo(7L, bob, false);

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(7L)).thenReturn(Optional.of(bobsTodo));

        // ACT & ASSERT
        assertThatThrownBy(() -> todoService.deleteTodo(7L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Todo not found");

        // ✅ The most important assertion: delete() must NEVER be called on someone else's todo.
        verify(todoRepository, never()).delete(any());
    }

    @Test
    void deleteTodo_notFound_throwsException() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.deleteTodo(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Todo not found");

        verify(todoRepository, never()).delete(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS — build test objects cleanly in one place
    // ─────────────────────────────────────────────────────────────────────────

    private User buildUser(Long id, String email) {
        User user = new User("Test User", email, "hashed-password");
        user.setId(id);
        return user;
    }

    private ToDo buildTodo(Long id, User owner, boolean completed) {
        ToDo todo = new ToDo();
        todo.setId(id);
        todo.setTitle("Todo " + id);
        todo.setDescription("Description " + id);
        todo.setCompleted(completed);
        todo.setUser(owner);
        todo.setCreatedAt(LocalDateTime.now());
        return todo;
    }
}

