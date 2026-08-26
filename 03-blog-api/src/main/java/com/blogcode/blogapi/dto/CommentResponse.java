package com.blogcode.blogapi.dto;

import com.blogcode.blogapi.entity.Comment;

import java.time.Instant;

/**
 * Read-only representation of a {@link Comment} returned to API clients.
 *
 * <p>Deliberately omits the parent post to avoid duplicating the entire {@code Post}
 * (including its comment list) inside every comment payload - clients already know which
 * post they asked about because it is part of the request URL.
 *
 * @param id        the comment's database identifier
 * @param author    the commenter's display name
 * @param content   the comment text
 * @param createdAt when the comment was created
 */
public record CommentResponse(
        Long id,
        String author,
        String content,
        Instant createdAt
) {

    /**
     * Factory method that maps a {@link Comment} entity into its public API representation.
     *
     * @param comment the entity to convert
     * @return an immutable response DTO describing the given comment
     */
    public static CommentResponse fromEntity(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getAuthor(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
