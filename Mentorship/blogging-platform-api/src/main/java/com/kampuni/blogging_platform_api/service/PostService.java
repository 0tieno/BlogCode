package com.kampuni.blogging_platform_api.service;

import com.kampuni.blogging_platform_api.dto.PostRequest;
import com.kampuni.blogging_platform_api.dto.PostResponse;

import java.util.List;

public interface PostService {

    PostResponse createPost(PostRequest postRequest);
    List<PostResponse> getAllPost();
    PostResponse getOne(Long id);
    PostResponse updatePost(Long id, PostRequest postRequest);
    void deletePost(Long id);
}
