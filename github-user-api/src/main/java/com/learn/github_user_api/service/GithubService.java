package com.learn.github_user_api.service;

import org.springframework.stereotype.Service;

@Service
public class GithubService {

    public String findUser(String username){
        return "Searching Github for " + username;
    }
}
