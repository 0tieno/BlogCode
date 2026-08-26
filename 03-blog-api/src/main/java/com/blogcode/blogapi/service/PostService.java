package com.blogcode.blogapi.service;

import com.blogcode.blogapi.dto.PostCreateRequest;
import com.blogcode.blogapi.dto.PostResponse;
import com.blogcode.blogapi.dto.PostUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service-layer contract for managing blog posts.
 *
 * <p>Defining a service <b>interface</b> separate from its implementation
 * ({@link com.blogcode.blogapi.service.impl.PostServiceImpl}) is a classic layered-
 * architecture pattern: controllers depend only on this abstraction, which makes the
 * business logic easy to mock in controller-layer tests and easy to swap out or extend
 * without touching any calling code.
 */
public interface PostService {

    /**
     * Retrieves a single page of posts.
     *
     * @param pageable paging and sorting information supplied by the client
     *                 (e.g. page number, page size, sort field/direction)
     * @return a page of {@link PostResponse} DTOs matching the requested paging/sorting
     */
    Page<PostResponse> getAllPosts(Pageable pageable);

    /**
     * Retrieves a single post by its identifier.
     *
     * @param id the post's database identifier
     * @return the matching post as a {@link PostResponse}
     * @throws com.blogcode.blogapi.exception.ResourceNotFoundException if no post with
     *         the given id exists
     */
    PostResponse getPostById(Long id);

    /**
     * Creates a new post.
     *
     * @param request validated request payload describing the new post
     * @return the newly created post as a {@link PostResponse}, including its generated id
     */
    PostResponse createPost(PostCreateRequest request);

    /**
     * Updates the title and content of an existing post.
     *
     * @param id      the identifier of the post to update
     * @param request validated request payload with the new title/content
     * @return the updated post as a {@link PostResponse}
     * @throws com.blogcode.blogapi.exception.ResourceNotFoundException if no post with
     *         the given id exists
     */
    PostResponse updatePost(Long id, PostUpdateRequest request);

    /**
     * Deletes a post and, via cascading, all of its comments.
     *
     * @param id the identifier of the post to delete
     * @throws com.blogcode.blogapi.exception.ResourceNotFoundException if no post with
     *         the given id exists
     */
    void deletePost(Long id);
}
