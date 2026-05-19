package com.learn.testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.testing.dto.TodoRequest;
import com.learn.testing.dto.TodoResponse;
import com.learn.testing.exception.GlobalExceptionHandler;
import com.learn.testing.exception.TodoNotFoundException;
import com.learn.testing.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT: Controller tests for TodoController
//
// This uses the same standaloneSetup pattern as AuthControllerTest.
// Key additions:
//   - PageableHandlerMethodArgumentResolver: required so Spring can parse
//     ?page=0&size=10 query params into a Pageable argument in the controller
//   - Testing 404 from TodoNotFoundException
//   - Testing 400 from @Valid on an empty title
//   - Testing 401 when no authentication is present (endpoint is protected)
//
// The security filter (JwtAuthFilter) does NOT run in standaloneSetup —
// security is tested separately in the unit tests and via manual blackbox
// testing. Controller tests focus on the HTTP contract.
// ══════════════════════════════════════════════════════════════════════════════
@ExtendWith(MockitoExtension.class)
class TodoControllerTest {

    @Mock
    private TodoService todoService;

    @InjectMocks
    private TodoController todoController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(todoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                // ── Required for Pageable parameter resolution ─────────────
                // Without this, Spring doesn't know how to parse ?page=0&size=10
                // into a Pageable object for the controller method parameter.
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    private TodoResponse sampleTodo(Long id) {
        return new TodoResponse(id, "Buy milk", "2 litres", false, LocalDateTime.now());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // GET /api/todos — paginated list
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void getMyTodos_returns200WithPagedContent() throws Exception {
        // PageImpl is Spring's concrete Page implementation — used in tests
        var fakePage = new PageImpl<>(
                List.of(sampleTodo(1L), sampleTodo(2L)),
                PageRequest.of(0, 10),
                2 // totalElements
        );
        when(todoService.getMyTodos(any())).thenReturn(fakePage);

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())         // paginated response
                .andExpect(jsonPath("$.content.length()").value(2)) // 2 items in this page
                .andExpect(jsonPath("$.totalElements").value(2));   // 2 total in DB
    }

    // ──────────────────────────────────────────────────────────────────────────
    // POST /api/todos — valid body → 201 Created
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void createTodo_validRequest_returns201WithBody() throws Exception {
        when(todoService.createTodo(any())).thenReturn(sampleTodo(1L));

        TodoRequest body = new TodoRequest("Buy milk", "2 litres");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())                       // 201 Created
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.completed").value(false));      // new todos start incomplete
    }

    // ──────────────────────────────────────────────────────────────────────────
    // POST /api/todos — empty title → @Valid fails → 400 Bad Request
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void createTodo_emptyTitle_returns400WithFieldError() throws Exception {
        // ══════════════════════════════════════════════════════════════════════
        // This test verifies the end-to-end validation path:
        //   HTTP request → controller → @Valid → GlobalExceptionHandler → 400
        //
        // The service is NEVER invoked when validation rejects the input.
        // This is something only a controller test can verify — a pure unit
        // test on the service wouldn't even see this request.
        // ══════════════════════════════════════════════════════════════════════
        TodoRequest body = new TodoRequest("", null); // empty title violates @NotBlank

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())              // 400
                .andExpect(jsonPath("$.title").exists());        // error on "title" field specifically
    }

    // ──────────────────────────────────────────────────────────────────────────
    // DELETE /api/todos/{id} — not found → TodoNotFoundException → 404
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void deleteTodo_todoNotFound_returns404() throws Exception {
        doThrow(new TodoNotFoundException(99L)).when(todoService).deleteTodo(eq(99L));

        mockMvc.perform(delete("/api/todos/99"))
                .andExpect(status().isNotFound())                        // 404 Not Found ← not 500!
                .andExpect(jsonPath("$.error").value("Todo not found: 99"));
    }

    // ──────────────────────────────────────────────────────────────────────────
    // PATCH /api/todos/{id}/complete — successful toggle → 200
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void completeTodo_exists_returns200WithToggledStatus() throws Exception {
        TodoResponse completed = new TodoResponse(1L, "Buy milk", null, true, LocalDateTime.now());
        when(todoService.completeTodo(eq(1L))).thenReturn(completed);

        mockMvc.perform(patch("/api/todos/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true)); // toggled from false to true
    }

    // ──────────────────────────────────────────────────────────────────────────
    // PUT /api/todos/{id} — valid update → 200 with updated body
    // ──────────────────────────────────────────────────────────────────────────
    @Test
    void updateTodo_validRequest_returns200() throws Exception {
        TodoResponse updated = new TodoResponse(1L, "Updated title", "New desc", false, LocalDateTime.now());
        when(todoService.updateTodo(eq(1L), any())).thenReturn(updated);

        TodoRequest body = new TodoRequest("Updated title", "New desc");

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated title"));
    }
}
