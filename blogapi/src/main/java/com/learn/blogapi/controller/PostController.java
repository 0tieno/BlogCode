package com.learn.blogapi.controller;

import com.learn.blogapi.dto.PostRequest;
import com.learn.blogapi.dto.PostResponse;
import com.learn.blogapi.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.createPost(postRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        PostResponse postResponse = postService.getPost(id);
        return ResponseEntity.ok(postResponse);
    }

//    @GetMapping
//    public ResponseEntity<List<PostResponse>> getAllPosts() {
//        postService.getPosts("term");
//    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.updatePost(id, postRequest);
        return ResponseEntity.ok(postResponse);
    }

    @DeleteMapping("/{id}")
    void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }
}
