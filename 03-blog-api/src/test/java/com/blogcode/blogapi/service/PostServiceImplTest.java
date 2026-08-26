package com.blogcode.blogapi.service;

import com.blogcode.blogapi.dto.PostCreateRequest;
import com.blogcode.blogapi.dto.PostResponse;
import com.blogcode.blogapi.entity.Post;
import com.blogcode.blogapi.exception.ResourceNotFoundException;
import com.blogcode.blogapi.repository.PostRepository;
import com.blogcode.blogapi.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pure unit tests for {@link PostServiceImpl}, using plain Mockito (no Spring context at
 * all) to keep the tests fast and focused purely on business logic: given a mocked
 * {@link PostRepository}, does the service behave correctly?
 */
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    /**
     * Verifies that creating a post maps the request fields onto a new {@link Post},
     * saves it, and returns a {@link PostResponse} reflecting the saved entity.
     */
    @Test
    void createPost_savesAndReturnsMappedResponse() {
        PostCreateRequest request = new PostCreateRequest("Title", "Content", "Author");
        Post saved = Post.builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .author("Author")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(postRepository.save(any(Post.class))).thenReturn(saved);

        PostResponse response = postService.createPost(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Title");
        verify(postRepository).save(any(Post.class));
    }

    /**
     * Verifies that requesting a post that does not exist raises
     * {@link ResourceNotFoundException} rather than returning {@code null} or throwing an
     * unrelated exception.
     */
    @Test
    void getPostById_whenMissing_throwsResourceNotFoundException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
