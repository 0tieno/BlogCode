package com.learn.spring101;

public record StudentDto(
        String firstname,
        String lastname,
        String email,
        Integer schoolId
) {
}
