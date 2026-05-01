package com.kampuni.blogging_platform_api.controller;


import com.kampuni.blogging_platform_api.dto.PostRequestDto;
import com.kampuni.blogging_platform_api.dto.PostResponseDto;
import com.kampuni.blogging_platform_api.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public PostResponseDto createPost(@Valid @RequestBody PostRequestDto postRequestDto){
        return postService.createPost(postRequestDto);
    }

    @GetMapping
    public List<PostResponseDto> getAllPosts(){
        return postService.getAllPost();
    }

    @PutMapping("/{id}")
    public PostResponseDto updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequestDto postRequestDto){
        return postService.updatePost(id, postRequestDto);
    }

    @DeleteMapping("/{id}")
    public void  deletePost(@PathVariable Long id){
        postService.deletePost(id);
    }
}
