package com.kampuni.userapi.service;

import com.kampuni.userapi.dto.UserRequestDto;
import com.kampuni.userapi.dto.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto createUser(UserRequestDto userRequestDto);

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUser(Long id);

    UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

    void delete(Long id);
}
