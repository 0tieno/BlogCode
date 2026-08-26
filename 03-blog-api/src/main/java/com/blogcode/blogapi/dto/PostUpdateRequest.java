package com.blogcode.blogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for updating an existing blog post via {@code PUT /api/v1/posts/{id}}.
 *
 * <p>This is intentionally a separate record from {@link PostCreateRequest}, even though
 * the fields look similar today. In a real application, update semantics often diverge
 * from create semantics (e.g. the author might not be editable after creation), so keeping
 * distinct DTOs per use case avoids having to retrofit one shared class later.
 *
 * @param title   the new post title; required, 3-200 characters
 * @param content the new post body; required, at least 1 character
 */
public record PostUpdateRequest(

        @NotBlank(message = "Title must not be blank")
        @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
        String title,

        @NotBlank(message = "Content must not be blank")
        String content
) {
}
