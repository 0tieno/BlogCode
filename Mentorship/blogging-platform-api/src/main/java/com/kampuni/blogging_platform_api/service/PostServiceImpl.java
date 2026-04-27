package com.kampuni.blogging_platform_api.service;

import com.kampuni.blogging_platform_api.dto.PostRequest;
import com.kampuni.blogging_platform_api.dto.PostResponse;
import com.kampuni.blogging_platform_api.entity.Post;
import com.kampuni.blogging_platform_api.repository.PostRespository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService{

    private final PostRespository postRespository;

    public PostServiceImpl(PostRespository postRespository) {
        this.postRespository = postRespository;
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {

        Post post = new Post();

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setCategory(postRequest.getCategory());
        post.setTags(postRequest.getTags());

        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRespository.save(post);
        return mapToResponse(savedPost);

    }

    @Override
    public List<PostResponse> getAllPost() {
        return postRespository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public PostResponse getOne(Long id) {
        Post post = postRespository.findById(id).orElseThrow(() -> new RuntimeException("post not found"));
        return mapToResponse(post);
    }

    @Override
    public PostResponse updatePost(
            Long id,
            @Valid @RequestBody PostRequest postRequest) {

        Post post = new Post();

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setCategory(postRequest.getCategory());
        post.setTags(postRequest.getTags());
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRespository.save(post);

        return mapToResponse(updatedPost);


    }

    @Override
    public void deletePost(Long id) {
        postRespository.deleteById(id);
    }

    private PostResponse mapToResponse(Post post){

        PostResponse postResponse = new PostResponse();

        postResponse.setId(post.getId());
        postResponse.setTitle(post.getTitle());
        postResponse.setContent(post.getContent());
        postResponse.setCategory(post.getCategory());
        postResponse.setTags(post.getTags());
        postResponse.setCreatedAt(post.getCreatedAt());
        postResponse.setUpdatedAt(post.getUpdatedAt());

        return postResponse;

    }
}
