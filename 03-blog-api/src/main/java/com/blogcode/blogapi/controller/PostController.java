package com.blogcode.blogapi.controller;

import com.blogcode.blogapi.dto.PostCreateRequest;
import com.blogcode.blogapi.dto.PostResponse;
import com.blogcode.blogapi.dto.PostUpdateRequest;
import com.blogcode.blogapi.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing CRUD endpoints for blog posts under {@code /api/v1/posts}.
 *
 * <p>Controllers in this architecture are intentionally "thin": they only handle HTTP
 * concerns (URL mapping, status codes, request/response bodies) and delegate every piece
 * of business logic to {@link PostService}. This separation is what makes it possible to
 * unit-test the service layer without spinning up a servlet container, and to test this
 * controller in isolation with a mocked service.
 */
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * Lists posts one page at a time.
     *
     * <p>Spring Data's {@link Pageable} is resolved automatically from query parameters
     * such as {@code ?page=0&size=10&sort=createdAt,desc}, so no manual parsing of paging
     * parameters is required. {@link PageableDefault} supplies sensible defaults (page
     * size 10, newest first) whenever the client omits those query parameters.
     *
     * @param pageable paging/sorting parameters, defaulting to page 0, size 10, sorted by
     *                 {@code createdAt} descending
     * @return {@code 200 OK} with a page of {@link PostResponse} objects
     */
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    /**
     * Retrieves a single post by its identifier.
     *
     * @param id the post's database identifier, taken from the URL path
     * @return {@code 200 OK} with the matching {@link PostResponse}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    /**
     * Creates a new post.
     *
     * <p>{@code @Valid} triggers Bean Validation on {@link PostCreateRequest} before this
     * method body ever runs; any constraint violation short-circuits into
     * {@link com.blogcode.blogapi.exception.GlobalExceptionHandler#handleValidation}.
     *
     * @param request the validated request body describing the new post
     * @return {@code 201 Created} with the newly created {@link PostResponse}
     */
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest request) {
        PostResponse created = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Updates an existing post's title and content.
     *
     * @param id      the identifier of the post to update, taken from the URL path
     * @param request the validated request body with the new title/content
     * @return {@code 200 OK} with the updated {@link PostResponse}
     */
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id,
                                                    @Valid @RequestBody PostUpdateRequest request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    /**
     * Deletes a post, along with all of its comments (via JPA cascading).
     *
     * @param id the identifier of the post to delete, taken from the URL path
     * @return {@code 204 No Content} to signal successful deletion with no response body
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
