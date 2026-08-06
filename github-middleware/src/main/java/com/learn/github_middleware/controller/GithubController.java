package com.learn.github_middleware.controller;


import com.learn.github_middleware.dto.UserResponse;
import com.learn.github_middleware.service.GithubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/github")
public class GithubController {

  private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}")
    public UserResponse getUser(@PathVariable String username){
        return githubService.getUser(username);
    }
}
