package com.learn.github_middleware.dto;

public record UserResponse(
        String username,
        String fullName,
        String biography,
        int followers,
        int following,
        int publicRepositories,
        String profileImage
) {
}
