package com.learn.github_user_api.controller;

import com.learn.github_user_api.service.GithubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/github/{username}")
public class HelloController {

    private final GithubService githubService;

    public HelloController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping
    public String getGithubUser(@PathVariable String username){
        return githubService.findUser(username);
    }
}
