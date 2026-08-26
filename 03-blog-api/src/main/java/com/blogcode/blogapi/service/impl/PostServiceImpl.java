package com.blogcode.blogapi.service.impl;

import com.blogcode.blogapi.dto.PostCreateRequest;
import com.blogcode.blogapi.dto.PostResponse;
import com.blogcode.blogapi.dto.PostUpdateRequest;
import com.blogcode.blogapi.entity.Post;
import com.blogcode.blogapi.exception.ResourceNotFoundException;
import com.blogcode.blogapi.repository.PostRepository;
import com.blogcode.blogapi.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of {@link PostService}.
 *
 * <p>Annotated {@code @Service} so Spring registers it as a bean and injects it wherever
 * {@link PostService} is required (e.g. in {@link com.blogcode.blogapi.controller.PostController}).
 * Lombok's {@code @RequiredArgsConstructor} generates a constructor for every {@code final}
 * field, which is the recommended way to perform constructor injection in Spring: it makes
 * dependencies explicit, immutable, and trivially mockable in unit tests, without needing
 * a hand-written constructor or field-level {@code @Autowired}.
 */
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    /**
     * {@inheritDoc}
     *
     * <p>Marked {@code @Transactional(readOnly = true)} because it only reads data: this
     * hints to Hibernate that it can skip dirty-checking and flush overhead, and it keeps
     * the persistence context open long enough for {@code PostResponse.fromEntity} to
     * safely call {@code post.getComments().size()} on the lazy collection.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostResponse::fromEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long id) {
        Post post = findPostOrThrow(id);
        return PostResponse.fromEntity(post);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Marked {@code @Transactional} (read-write) so the insert either fully succeeds or
     * is fully rolled back - there is no scenario where a partially-created post should
     * remain visible to other requests.
     */
    @Override
    @Transactional
    public PostResponse createPost(PostCreateRequest request) {
        Post post = Post.builder()
                .title(request.title())
                .content(request.content())
                .author(request.author())
                .build();
        Post saved = postRepository.save(post);
        return PostResponse.fromEntity(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Because the {@code post} entity was loaded inside this same transaction, simply
     * mutating its fields is enough - Hibernate's dirty checking detects the change and
     * issues an {@code UPDATE} automatically when the transaction commits, without an
     * explicit call to {@code save()}.
     */
    @Override
    @Transactional
    public PostResponse updatePost(Long id, PostUpdateRequest request) {
        Post post = findPostOrThrow(id);
        post.setTitle(request.title());
        post.setContent(request.content());
        return PostResponse.fromEntity(post);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = findPostOrThrow(id);
        postRepository.delete(post);
    }

    /**
     * Shared helper that loads a post by id or raises a {@link ResourceNotFoundException},
     * avoiding duplicated "find or throw" logic across every method above.
     *
     * @param id the post identifier to look up
     * @return the matching, managed {@link Post} entity
     * @throws ResourceNotFoundException if no post with the given id exists
     */
    private Post findPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
    }
}
