package com.learn.github_middleware.service;

import com.learn.github_middleware.client.GithubClient;
import com.learn.github_middleware.dto.GithubUserDto;
import com.learn.github_middleware.dto.UserResponse;
import org.springframework.stereotype.Service;

@Service
public class GithubService {

   private final GithubClient githubClient;

    public GithubService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }


    public UserResponse getUser(String username){

        GithubUserDto githubUser = githubClient.getUser(username);

        return new UserResponse(
                githubUser.login(),
                githubUser.name(),
                githubUser.bio(),
                githubUser.publicRepos(),
                githubUser.followers(),
                githubUser.following(),
                githubUser.avatarUrl()
        );

    }
}
