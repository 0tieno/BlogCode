package com.learn.github_middleware.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubUserDto(
        String login,
        String name,
        String bio,
        @JsonProperty("public_repos")
        int publicRepos,
        int followers,
        int following,
        @JsonProperty("avatar_url")
        String avatarUrl
) {
}
