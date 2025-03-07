package com.authserver.service;

import com.authserver.dto.UserRequestDTO;
import com.authserver.exceptions.UserExistException;
import com.authserver.model.User;
import com.authserver.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean saveUser(UserRequestDTO userRequestDTO) {
        User user = new User(userRequestDTO.getUsername(),
                passwordEncoder.encode(userRequestDTO.getPassword()),
                userRequestDTO.getEmail());

        checkEmailIsUnique(user);
        checkUsernameIsUnique(user);
        userRepository.save(user);
        return true;
    }


    private void checkEmailIsUnique(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserExistException("Email already exists!");
        }
    }

    private void checkUsernameIsUnique(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserExistException("Username already exists!");
        }
    }

}
