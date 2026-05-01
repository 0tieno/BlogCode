package com.kampuni.userapi.service;

import com.kampuni.userapi.dto.UserRequestDto;
import com.kampuni.userapi.dto.UserResponseDto;
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
    public UserResponseDto createUser(UserRequestDto userRequestDto) {

        User user = new User();

        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());
        user.setAge(userRequestDto.getAge());

        User savedUser = userRepository.save(user);
        return mapToResponse (savedUser);
    }


    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserResponseDto getUser(Long id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(()-> new RuntimeException("User Not Found"));
        return mapToResponse(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {

        User user = userRepository
                .findById(id)
                .orElseThrow(()->new RuntimeException("User Not Found"));

        user.setAge(userRequestDto.getAge());
        user.setName(userRequestDto.getName());
        user.setEmail(userRequestDto.getEmail());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponseDto mapToResponse(User user){
        UserResponseDto userResponseDto = new UserResponseDto();

        userResponseDto.setId(user.getId());
        userResponseDto.setAge(user.getAge());
        userResponseDto.setName(user.getName());
        userResponseDto.setEmail(user.getEmail());

        return userResponseDto;
    }

}
