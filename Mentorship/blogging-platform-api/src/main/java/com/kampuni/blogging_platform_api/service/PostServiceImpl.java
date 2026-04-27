package com.kampuni.blogging_platform_api.service;

import com.kampuni.blogging_platform_api.dto.PostRequest;
import com.kampuni.blogging_platform_api.dto.PostResponse;
import com.kampuni.blogging_platform_api.repository.PostRespository;

import java.util.List;

public class PostServiceImpl implements PostService{


    private final PostRespository postRespository;

    public PostServiceImpl(PostRespository postRespository) {
        this.postRespository = postRespository;
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {
        return null;
    }

    @Override
    public List<PostResponse> getAllPost() {
        return List.of();
    }

    @Override
    public PostResponse getOne(Long id) {
        return null;
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest postRequest) {
        return null;
    }

    @Override
    public void deletePost(Long id) {

    }
}
