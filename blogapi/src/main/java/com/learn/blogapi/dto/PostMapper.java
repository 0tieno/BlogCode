package com.learn.blogapi.dto;

import com.learn.blogapi.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostMapper {
    public Post toEntity(PostRequest request) {
        Post post = new Post();

        post.setTitle(request.title());
        post.setContent(request.content());
        post.setCategory(request.category());
        post.setTags(request.tags());

        return post;

    }

    public  PostResponse toResponse(Post post) {
        return new PostResponse(
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
