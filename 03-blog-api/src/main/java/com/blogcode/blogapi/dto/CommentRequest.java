package com.blogcode.blogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for adding a new comment to a post via
 * {@code POST /api/v1/posts/{postId}/comments}.
 *
 * @param author  the display name of whoever is commenting; required
 * @param content the comment text; required, up to 2000 characters
 */
public record CommentRequest(

        @NotBlank(message = "Author must not be blank")
        @Size(max = 100, message = "Author name must be at most 100 characters")
        String author,

        @NotBlank(message = "Content must not be blank")
        @Size(max = 2000, message = "Comment content must be at most 2000 characters")
        String content
) {
}
