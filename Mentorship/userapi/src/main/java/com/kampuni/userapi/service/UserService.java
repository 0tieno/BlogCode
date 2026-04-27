package com.kampuni.userapi.service;

import com.kampuni.userapi.dto.UserRequest;
import com.kampuni.userapi.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest userRequest);

    List<UserResponse> getAllUsers();

    UserResponse getUser(Long id);

    UserResponse updateUser(Long id, UserRequest userRequest);

    void delete(Long id);
}
