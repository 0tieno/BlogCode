package com.learn.testing.service;

import com.learn.testing.dto.TodoRequest;
import com.learn.testing.dto.TodoResponse;
import com.learn.testing.entity.Todo;
import com.learn.testing.entity.User;
import com.learn.testing.repository.TodoRepository;
import com.learn.testing.repository.UserRepository;
import com.learn.testing.exception.TodoNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ══════════════════════════════════════════════════════════════════════════════
// This is the most interesting test class.
// TodoService reads the logged-in user from the SecurityContext.
// In unit tests, there's no HTTP request and no JWT filter — so WE must
// put the user into the SecurityContext manually.
// ══════════════════════════════════════════════════════════════════════════════
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock TodoRepository todoRepository;
    @Mock UserRepository userRepository;
    @InjectMocks TodoService todoService;

    private User alice;
    private User bob;

    @BeforeEach
    void setUp() {
        alice = buildUser(1L, "alice@test.com");
        bob   = buildUser(2L, "bob@test.com");

        // ══════════════════════════════════════════════════════════════════
        // CONCEPT: Simulating a logged-in user in a unit test
        //
        // In production: JwtAuthFilter puts the user in SecurityContextHolder.
        // In unit tests:  we put them there manually so TodoService can read it.
        //
        // UsernamePasswordAuthenticationToken is Spring Security's standard
        // way to represent "this user is authenticated".
        // ══════════════════════════════════════════════════════════════════
        var auth = new UsernamePasswordAuthenticationToken("alice@test.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        // ALWAYS clear the SecurityContext after each test.
        // Otherwise, the "logged in" user from one test bleeds into the next.
        SecurityContextHolder.clearContext();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // getMyTodos()
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void getMyTodos_returnsOnlyAlicesTodos() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findByUserId(1L, pageable)).thenReturn(new PageImpl<>(List.of(
                buildTodo(1L, alice, false),
                buildTodo(2L, alice, true)
        )));

        var result = todoService.getMyTodos(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(1).completed()).isTrue();
        verify(todoRepository).findByUserId(1L, pageable);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // createTodo()
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void createTodo_savesAndReturnsMappedResponse() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.save(any(Todo.class))).thenAnswer(inv -> {
            Todo t = inv.getArgument(0);
            t.setId(99L);
            t.setCreatedAt(LocalDateTime.now()); // simulate @PrePersist (doesn't run in unit tests)
            return t;
        });

        TodoResponse response = todoService.createTodo(new TodoRequest("Buy milk", "2 litres"));

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.title()).isEqualTo("Buy milk");
        assertThat(response.completed()).isFalse(); // new todos are always incomplete
    }

    // ──────────────────────────────────────────────────────────────────────────
    // updateTodo() — ownership check
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void updateTodo_ownedByAlice_updatesSuccessfully() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(10L)).thenReturn(Optional.of(buildTodo(10L, alice, false)));
        when(todoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TodoResponse response = todoService.updateTodo(10L, new TodoRequest("New title", "New desc"));

        assertThat(response.title()).isEqualTo("New title");
    }

    @Test
    void updateTodo_ownedByBob_aliceCannotUpdate() {
        // ARRANGE: the todo belongs to BOB, but ALICE is logged in
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(10L)).thenReturn(Optional.of(buildTodo(10L, bob, false)));

        // ACT & ASSERT: Alice cannot touch Bob's todo
        assertThatThrownBy(() -> todoService.updateTodo(10L, new TodoRequest("hacked", null)))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("10"); // IDOR prevention — deliberately vague, contains the id

        verify(todoRepository, never()).save(any()); // must never save
    }

    // ──────────────────────────────────────────────────────────────────────────
    // completeTodo() — toggle test
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void completeTodo_notCompleted_togglesToTrue() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(5L)).thenReturn(Optional.of(buildTodo(5L, alice, false)));
        when(todoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(todoService.completeTodo(5L).completed()).isTrue();
    }

    @Test
    void completeTodo_alreadyCompleted_togglesToFalse() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(5L)).thenReturn(Optional.of(buildTodo(5L, alice, true)));
        when(todoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(todoService.completeTodo(5L).completed()).isFalse();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // deleteTodo() — ownership and not-found
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void deleteTodo_ownedByAlice_deletesSuccessfully() {
        Todo todo = buildTodo(7L, alice, false);
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(7L)).thenReturn(Optional.of(todo));

        todoService.deleteTodo(7L);

        verify(todoRepository).delete(todo); // confirm the right todo was deleted
    }

    @Test
    void deleteTodo_ownedByBob_aliceCannotDelete() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(7L)).thenReturn(Optional.of(buildTodo(7L, bob, false)));

        assertThatThrownBy(() -> todoService.deleteTodo(7L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("7");

        // The most critical assertion: delete must NEVER be called on another user's data
        verify(todoRepository, never()).delete(any());
    }

    @Test
    void deleteTodo_notFound_throwsException() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(alice));
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.deleteTodo(999L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("999");

        verify(todoRepository, never()).delete(any());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helper methods — build test objects in one place
    // ──────────────────────────────────────────────────────────────────────────
    private User buildUser(Long id, String email) {
        User u = new User("Test", email, "hashed");
        u.setId(id);
        return u;
    }

    private Todo buildTodo(Long id, User owner, boolean completed) {
        Todo t = new Todo();
        t.setId(id);
        t.setTitle("Todo " + id);
        t.setDescription("Desc " + id);
        t.setCompleted(completed);
        t.setUser(owner);
        t.setCreatedAt(LocalDateTime.now());
        return t;
    }
}

