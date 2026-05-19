package com.learn.todoapi.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/greet")
public class GreetingController {

    @GetMapping
    public String hello(){
        return "hello world";
    }


    //pathVariable
    @GetMapping("/{name}")
    public String greetByName(
            @PathVariable String name
    ){
        return "hello, " + name;
    }

    //RequestParams

    @GetMapping("/search")
    public String greetWithParam(@RequestParam (defaultValue = "Stranger") String name){
        return "Hello " + name + " from query param";
    }

//    @PostMapping
//    public String customGreet(@RequestBody GreetRequest greetRequest){
//        return greetRequest.greeting() + ", " + greetRequest.name() + "!";
//    }

    @PostMapping
    public ResponseEntity<String> customGreet(@RequestBody GreetRequest greetRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(greetRequest.greeting() + ", " + greetRequest.name() + "!");
    }

    @GetMapping("/status")
    public ResponseEntity<String> statusDemo(){
        return ResponseEntity
                .ok("Everything is fine");
    }


    public record GreetRequest(
            String name,
            String greeting
    ){};

}
