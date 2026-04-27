package com.kampuni.blogging_platform_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
    private List<String> tags;
    private String category;
}
