package com.kampuni.blogging_platform_api.repository;

import com.kampuni.blogging_platform_api.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRespository extends JpaRepository<Post, Long> {

    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrCategoryContainingIgnoreCase
            (
                    String title, String content, String category
            );
}
