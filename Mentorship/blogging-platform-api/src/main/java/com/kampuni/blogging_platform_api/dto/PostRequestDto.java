package com.kampuni.blogging_platform_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostRequestDto(

        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must not exceed 100 characters")
        String title,

        @NotBlank(message = "Content is required")
        String content,

        @NotBlank(message = "Category is required")
        String category,

        @NotEmpty(message = "Tags cannot be empty")
        List<@NotBlank(message = "Tag cannot be blank") String> tags

) {}