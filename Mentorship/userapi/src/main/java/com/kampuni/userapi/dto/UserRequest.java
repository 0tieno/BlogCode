package com.kampuni.userapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

    @Email
    private String email;

    @NotBlank
    private String name;

    @Min(1)
    private int age;
}
