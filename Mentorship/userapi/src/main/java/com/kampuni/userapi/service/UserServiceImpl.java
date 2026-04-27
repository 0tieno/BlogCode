package com.kampuni.userapi.service;

import com.kampuni.userapi.dto.UserRequest;
import com.kampuni.userapi.dto.UserResponse;
import com.kampuni.userapi.entity.User;
import com.kampuni.userapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {

        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setAge(userRequest.getAge());

        User savedUser = userRepository.save(user);
        return mapToResponse (savedUser);
    }


    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponse getUser(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(()-> new RuntimeException("User Not Found"));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository
                .findById(id).orElseThrow(()->new RuntimeException("User Not Found"));
        user.setAge(userRequest.getAge());
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user){
        UserResponse userResponse = new UserResponse();

        userResponse.setId(user.getId());
        userResponse.setAge(user.getAge());
        userResponse.setName(user.getName());
        userResponse.setEmail(user.getEmail());

        return userResponse;
    }

}
