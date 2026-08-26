package com.blogcode.blogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a new blog post via {@code POST /api/v1/posts}.
 *
 * <p>Implemented as a Java {@code record} because it is an immutable data carrier with no
 * behaviour of its own - records automatically generate the constructor, accessors,
 * {@code equals}/{@code hashCode} and {@code toString} that a plain DTO class would
 * otherwise need boilerplate for.
 *
 * <p>Keeping request DTOs separate from the {@link com.blogcode.blogapi.entity.Post} entity
 * (rather than exposing the entity directly in the controller) is a deliberate layering
 * choice: it stops clients from setting fields like {@code id} or {@code createdAt} that
 * should only ever be managed by the server, and it lets the validation rules for
 * "creating a post" evolve independently of the database schema.
 *
 * @param title   the post title; required, 3-200 characters
 * @param content the post body; required, at least 1 character
 * @param author  the display name of the post's author; required
 */
public record PostCreateRequest(

        @NotBlank(message = "Title must not be blank")
        @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
        String title,

        @NotBlank(message = "Content must not be blank")
        String content,

        @NotBlank(message = "Author must not be blank")
        @Size(max = 100, message = "Author name must be at most 100 characters")
        String author
) {
}
