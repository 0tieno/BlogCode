package com.learn.blogapi.service.impl;

import com.learn.blogapi.mapper.PostMapper;
import com.learn.blogapi.dto.PostRequest;
import com.learn.blogapi.dto.PostResponse;
import com.learn.blogapi.entity.Post;
import com.learn.blogapi.repository.PostRepository;
import com.learn.blogapi.service.PostService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {

        Post post = postMapper.toEntity(postRequest);

        LocalDateTime now = LocalDateTime.now();

        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        Post savedPost = postRepository.save(post);
        return postMapper.toResponse(savedPost);
    }

    @Override
    public PostResponse getPost(Long id) {
        return null;
    }

    @Override
    public List<PostResponse> getPosts(String term) {
        return List.of();
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest postRequest) {
        return null;
    }

    @Override
    public void deletePost(Long id) {

    }
}
