package com.learn.blogapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @NotBlank(message = "Content is required")
        String content,

        @NotBlank(message = "Category is required")
        String category,

        @NotEmpty(message = "At least one tag is required")
        List<@NotBlank(message = "Tag cannot be blank") String> tags
) {
}
