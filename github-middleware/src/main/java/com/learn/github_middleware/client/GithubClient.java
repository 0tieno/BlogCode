package com.learn.github_middleware.client;

import com.learn.github_middleware.dto.GithubUserDto;
import com.learn.github_middleware.exception.GithubUserNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class GithubClient {

    private final RestClient restClient;

    public GithubClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public GithubUserDto getUser(String username){

        try {
            return restClient.get()
                    .uri("/users/{username}", username)
                    .retrieve()
                    .body(GithubUserDto.class);
        }catch (HttpClientErrorException.NotFound ex){
            throw new GithubUserNotFoundException(username);
        }

    }
}
