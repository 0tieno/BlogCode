package com.kampuni.blogging_platform_api.service;

import com.kampuni.blogging_platform_api.dto.PostRequestDto;
import com.kampuni.blogging_platform_api.dto.PostResponseDto;
import com.kampuni.blogging_platform_api.entity.Post;
import com.kampuni.blogging_platform_api.exception.ResourceNotFoundException;
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

        post.setTitle(postRequestDto.title());
        post.setContent(postRequestDto.content());
        post.setCategory(postRequestDto.category());
        post.setTags(postRequestDto.tags());

        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        Post savedPost = postRespository.save(post);
        return mapToResponse(savedPost);

    }

    @Override
    public List<PostResponseDto> getAllPosts(String term) {

        List<Post> posts;

        if (term != null && !term.isEmpty()){
            posts = postRespository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrCategoryContainingIgnoreCase(term, term, term);
        }else {
            posts = postRespository.findAll();
        }
        return posts
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PostResponseDto getOne(Long id) {
        Post post = postRespository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post Not found"));
        return mapToResponse(post);
    }

    @Override
    public PostResponseDto updatePost(
            Long id,
            PostRequestDto postRequestDto) {

        Post post = postRespository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("post not found"));

        post.setTitle(postRequestDto.title());
        post.setContent(postRequestDto.content());
        post.setCategory(postRequestDto.category());
        post.setTags(postRequestDto.tags());
        post.setUpdatedAt(LocalDateTime.now());

        Post updatedPost = postRespository.save(post);

        return mapToResponse(updatedPost);

    }

    @Override
    public void deletePost(Long id) {
        postRespository.deleteById(id);
    }

    private PostResponseDto mapToResponse(Post post) {

        return new PostResponseDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                post.getTags(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
