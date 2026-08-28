package com.learn.todoapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TodoRequest(

        @NotBlank
        @Size(max = 50)
        String title,

        @Size(max = 100)
        String description
) {
}
