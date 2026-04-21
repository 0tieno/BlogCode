package com.kampuni.userapi;


import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user){

        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("User Already Exists");
        }
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public void deleteUserById(Long id){
        userRepository.deleteById(id);
    }

}
