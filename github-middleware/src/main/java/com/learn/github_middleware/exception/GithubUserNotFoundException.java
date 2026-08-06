package com.learn.github_middleware.exception;

public class GithubUserNotFoundException extends RuntimeException{

    public GithubUserNotFoundException(String username){
        super("Github user" + " " + username + "was not found");
    }
}
