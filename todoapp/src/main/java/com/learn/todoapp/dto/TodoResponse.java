package com.learn.todoapp.dto;

public record TodoResponse (
        Long id,
        String title,
        String description,
        boolean completed
){
}
