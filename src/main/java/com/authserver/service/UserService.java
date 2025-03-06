package com.authserver.service;

import com.authserver.dto.UserRequestDTO;
import com.authserver.exceptions.UserExistException;
import com.authserver.model.User;
import com.authserver.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean saveUser(UserRequestDTO userRequestDTO) {
        this.checkForNullUserRequestDTO(userRequestDTO);

        User user = new User(userRequestDTO.getUsername(),
                passwordEncoder.encode(userRequestDTO.getPassword()),
                userRequestDTO.getEmail());

        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new UserExistException("User already exists!");
        }
        userRepository.save(user);
        return true;
    }

    private void checkForNullUserRequestDTO(UserRequestDTO userRequestDTO){
        if (userRequestDTO == null) {
            throw new IllegalArgumentException("UserRequestDTO must be not null");
        }
        if(Objects.isNull(userRequestDTO.getEmail())){
            throw new IllegalArgumentException("E-mail must be not null");
        }
        if(Objects.isNull(userRequestDTO.getUsername())){
            throw new IllegalArgumentException("Username must be not null");
        }
        if(Objects.isNull(userRequestDTO.getPassword())){
            throw new IllegalArgumentException("Password must be not null");
        }
    }
}
