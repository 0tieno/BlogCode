package com.kampuni.blogging_platform_api.service;

import com.kampuni.blogging_platform_api.dto.PostRequestDto;
import com.kampuni.blogging_platform_api.dto.PostResponseDto;

import java.util.List;

public interface PostService {

    PostResponseDto createPost(PostRequestDto postRequestDto);
    List<PostResponseDto> getAllPost();
    PostResponseDto getOne(Long id);
    PostResponseDto updatePost(Long id, PostRequestDto postRequestDto);
    void deletePost(Long id);
}
