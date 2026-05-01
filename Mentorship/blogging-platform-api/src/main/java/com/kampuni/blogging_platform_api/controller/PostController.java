package com.kampuni.blogging_platform_api.controller;


import com.kampuni.blogging_platform_api.dto.PostRequestDto;
import com.kampuni.blogging_platform_api.dto.PostResponseDto;
import com.kampuni.blogging_platform_api.service.PostService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostRequestDto postRequestDto){

        PostResponseDto postResponseDto = postService.createPost(postRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts(@RequestParam(required = false) String term){
        return ResponseEntity.ok(postService.getAllPosts(term));
    }

    @GetMapping("/{id}")

    public ResponseEntity<PostResponseDto> getOnePost(@PathVariable Long id){
        return ResponseEntity.ok(postService.getOne(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequestDto postRequestDto){
        return ResponseEntity.ok(postService.updatePost(id, postRequestDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deletePost(@PathVariable Long id){
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
