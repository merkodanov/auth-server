package com.authserver.service;

import com.authserver.exceptions.UserExistException;
import com.authserver.model.User;
import com.authserver.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean saveUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null){
            throw new UserExistException("User already exists!");
        }
        userRepository.save(user);
        return true;
    }
}
