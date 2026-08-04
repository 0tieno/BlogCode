package com.learn.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @GetMapping("/greet/{userName}")
    public String greetUser(@PathVariable String userName){
        return "Hello" + " " + userName + "," + "how are you?";

    }

}
