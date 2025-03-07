package com.authserver.service;

import com.authserver.dto.UserRequestDTO;
import com.authserver.exceptions.InvalidEmailException;
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
        checkUserRequestDTONotNull(userRequestDTO);
        checkAtInEmail(userRequestDTO.getEmail());
        User user = new User(userRequestDTO.getUsername(),
                passwordEncoder.encode(userRequestDTO.getPassword()),
                userRequestDTO.getEmail());

        checkEmailIsUnique(user);
        checkUsernameIsUnique(user);
        userRepository.save(user);
        return true;
    }

    private void checkUserRequestDTONotNull(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            throw new IllegalArgumentException("UserRequestDTO must be not null");
        }
        if (Objects.isNull(userRequestDTO.getEmail())) {
            throw new IllegalArgumentException("E-mail must be not null");
        }
        if (Objects.isNull(userRequestDTO.getUsername())) {
            throw new IllegalArgumentException("Username must be not null");
        }
        if (Objects.isNull(userRequestDTO.getPassword())) {
            throw new IllegalArgumentException("Password must be not null");
        }
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

    private void checkAtInEmail(String email) {
        if (!email.contains("@") || email.indexOf("@") != email.lastIndexOf("@")) {
            throw new InvalidEmailException("E-mail invalid");
        }
        int indexOfAt = email.indexOf("@");
        String emailAfterAt = email.substring(indexOfAt);
        if (!emailAfterAt.contains(".") || emailAfterAt.indexOf(".") != emailAfterAt.lastIndexOf(".")){
            throw new InvalidEmailException("E-mail invalid");
        }
    }

}
