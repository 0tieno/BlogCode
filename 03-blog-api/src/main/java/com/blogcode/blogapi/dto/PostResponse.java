package com.blogcode.blogapi.dto;

import com.blogcode.blogapi.entity.Post;

import java.time.Instant;

/**
 * Read-only representation of a {@link Post} returned to API clients.
 *
 * <p>Rather than serialising the JPA entity straight out of the controller, the service
 * layer maps it into this record. That separation matters for three reasons that beginners
 * should internalise early:
 * <ol>
 *     <li>It prevents lazy-loading exceptions: {@code comments} is never touched here, only
 *     a pre-computed {@code commentCount} is exposed.</li>
 *     <li>It decouples the public API contract from internal persistence details, so the
 *     database schema can change without breaking clients.</li>
 *     <li>It avoids accidentally leaking internal-only fields.</li>
 * </ol>
 *
 * @param id          the post's database identifier
 * @param title       the post title
 * @param content     the post body
 * @param author      the post author's display name
 * @param commentCount how many comments currently exist on this post
 * @param createdAt   when the post was first created
 * @param updatedAt   when the post was last modified
 */
public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        int commentCount,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Factory method that maps a {@link Post} entity into its public API representation.
     * Centralising the mapping here (instead of repeating it in every service method)
     * keeps the entity-to-DTO conversion in exactly one place.
     *
     * @param post the entity to convert
     * @return an immutable response DTO describing the given post
     */
    public static PostResponse fromEntity(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getComments().size(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
