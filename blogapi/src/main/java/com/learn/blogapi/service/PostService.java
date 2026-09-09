package com.learn.blogapi.service;

import com.learn.blogapi.dto.PostRequest;
import com.learn.blogapi.dto.PostResponse;

import java.util.List;

public interface PostService {
    PostResponse createPost(PostRequest postRequest);
    PostResponse getPost(Long id);
    List<PostResponse> getPosts(String term);
    PostResponse updatePost(Long id, PostRequest postRequest);
    void deletePost(Long id);
}
