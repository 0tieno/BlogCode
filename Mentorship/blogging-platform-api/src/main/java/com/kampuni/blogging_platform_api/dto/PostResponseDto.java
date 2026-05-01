package com.kampuni.blogging_platform_api.dto;


import java.time.LocalDateTime;
import java.util.List;


public record PostResponseDto(
        Long id,
        String title,
        String content,
        String category,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
