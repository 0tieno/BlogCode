package com.blogcode.blogapi.controller;

import com.blogcode.blogapi.dto.PostCreateRequest;
import com.blogcode.blogapi.dto.PostResponse;
import com.blogcode.blogapi.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Slice test for {@link PostController} using {@code @WebMvcTest}, which boots only the
 * web layer (controllers, filters, JSON serialization) instead of the full application
 * context - much faster than a full {@code @SpringBootTest} and focused purely on HTTP
 * request/response behaviour.
 *
 * <p>{@link PostService} is replaced with a Mockito mock via {@code @MockitoBean} so this
 * test can verify controller behaviour (status codes, JSON shape, validation wiring)
 * completely independently of the real service/database logic.
 */
@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PostService postService;

    /**
     * Verifies that {@code GET /api/v1/posts} returns {@code 200 OK} with a JSON page
     * body containing the posts supplied by the mocked service.
     */
    @Test
    void getAllPosts_returnsOkWithPageOfPosts() throws Exception {
        PostResponse response = new PostResponse(1L, "Title", "Content", "Author", 0,
                Instant.now(), Instant.now());
        Page<PostResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        when(postService.getAllPosts(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Title"));
    }

    /**
     * Verifies that {@code POST /api/v1/posts} with a blank title is rejected with
     * {@code 400 Bad Request} before ever reaching the mocked service, proving that Bean
     * Validation is correctly wired into the controller.
     */
    @Test
    void createPost_withBlankTitle_returnsBadRequest() throws Exception {
        PostCreateRequest invalidRequest = new PostCreateRequest("", "Some content", "Author");

        mockMvc.perform(post("/api/v1/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());
    }

    /**
     * Verifies that {@code POST /api/v1/posts} with a valid payload returns
     * {@code 201 Created} and echoes back the post created by the mocked service.
     */
    @Test
    void createPost_withValidPayload_returnsCreated() throws Exception {
        PostCreateRequest validRequest = new PostCreateRequest("A valid title", "Some content", "Author");
        PostResponse response = new PostResponse(1L, "A valid title", "Some content", "Author", 0,
                Instant.now(), Instant.now());
        when(postService.createPost(eq(validRequest))).thenReturn(response);

        mockMvc.perform(post("/api/v1/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}
