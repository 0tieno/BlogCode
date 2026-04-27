package com.kampuni.blogging_platform_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private String category;
    private List<String> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
