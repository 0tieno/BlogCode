package com.kampuni.blogging_platform_api.service;

import com.kampuni.blogging_platform_api.dto.PostRequestDto;
import com.kampuni.blogging_platform_api.dto.PostResponseDto;
import com.kampuni.blogging_platform_api.entity.Post;
import com.kampuni.blogging_platform_api.repository.PostRespository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService{

    private final PostRespository postRespository;

    public PostServiceImpl(PostRespository postRespository) {
        this.postRespository = postRespository;
    }

    @Override
    public PostResponseDto createPost(PostRequestDto postRequestDto) {

        Post post = new Post();

        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setCategory(postRequestDto.getCategory());
        post.setTags(postRequestDto.getTags());

        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRespository.save(post);
        return mapToResponse(savedPost);

    }

    @Override
    public List<PostResponseDto> getAllPost() {
        return postRespository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PostResponseDto getOne(Long id) {
        Post post = postRespository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("post not found"));
        return mapToResponse(post);
    }

    @Override
    public PostResponseDto updatePost(
            Long id,
            PostRequestDto postRequestDto) {

        Post post = postRespository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("post not found"));

        post.setTitle(postRequestDto.getTitle());
        post.setContent(postRequestDto.getContent());
        post.setCategory(postRequestDto.getCategory());
        post.setTags(postRequestDto.getTags());
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRespository.save(post);

        return mapToResponse(updatedPost);

    }

    @Override
    public void deletePost(Long id) {
        postRespository.deleteById(id);
    }

    private PostResponseDto mapToResponse(Post post){

        PostResponseDto postResponseDto = new PostResponseDto();

        postResponseDto.setId(post.getId());
        postResponseDto.setTitle(post.getTitle());
        postResponseDto.setContent(post.getContent());
        postResponseDto.setCategory(post.getCategory());
        postResponseDto.setTags(post.getTags());
        postResponseDto.setCreatedAt(post.getCreatedAt());
        postResponseDto.setUpdatedAt(post.getUpdatedAt());

        return postResponseDto;

    }
}
