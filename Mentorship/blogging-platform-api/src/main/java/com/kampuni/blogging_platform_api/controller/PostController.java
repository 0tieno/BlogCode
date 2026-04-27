package com.kampuni.blogging_platform_api.controller;


import com.kampuni.blogging_platform_api.dto.PostRequest;
import com.kampuni.blogging_platform_api.dto.PostResponse;
import com.kampuni.blogging_platform_api.service.PostService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public PostResponse createPost(@Valid @RequestBody PostRequest postRequest){
        return postService.createPost(postRequest);
    }

    @GetMapping
    public List<PostResponse> getAllPosts(){
        return postService.getAllPost();
    }

    @PutMapping("/{id}")
    public PostResponse updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest postRequest){
        return postService.updatePost(id, postRequest);
    }

    @DeleteMapping("/{id}")
    public void  deletePost(
            @PathVariable Long id
    ){
        postService.deletePost(id);
    }
}
