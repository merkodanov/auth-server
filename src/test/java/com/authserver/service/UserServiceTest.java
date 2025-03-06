package com.authserver.service;

import com.authserver.dto.UserRequestDTO;
import com.authserver.exceptions.UserExistException;
import com.authserver.model.User;
import com.authserver.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private User user;
    private UserRequestDTO userRequestDTO;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void initUser() {
        userRequestDTO = new UserRequestDTO("Vlad",
                "qwerty123",
                "email@gmail.com");
        user = new User(userRequestDTO.getUsername(),
                userRequestDTO.getPassword(),
                userRequestDTO.getEmail());
    }

    static Stream<UserRequestDTO> userRequestDTOStream() {
        return Stream.of(
                new UserRequestDTO("fill", "fill", null),
                new UserRequestDTO("fill", null, "fill"),
                new UserRequestDTO(null, "fill", "fill")
        );
    }

    @ParameterizedTest
    @MethodSource("userRequestDTOStream")
    void saving_user_when_UserRequestDTO_fields_are_null(UserRequestDTO userRequestDTO) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.saveUser(userRequestDTO));
    }

    @Test
    void saving_user_when_UserRequestDTO_is_null() {
        userRequestDTO = null;

        Assertions.assertThrows(IllegalArgumentException.class, () -> userService.saveUser(userRequestDTO));
    }

    @Test
    void saving_user_when_user_already_exists() {
        Mockito.when(userRepository.findByEmail(Mockito.any(String.class))).thenReturn(user);

        Assertions.assertThrows(UserExistException.class, () -> userService.saveUser(userRequestDTO));
    }

    @Test
    void saving_user_is_success() {
        Mockito.when(userRepository.findByEmail(Mockito.any(String.class))).thenReturn(null);
        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("ENCODED");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(Mockito.any(User.class));

        boolean result = userService.saveUser(userRequestDTO);

        Assertions.assertTrue(result);
    }

    @Test
    void saving_user_without_at_in_email(){

    }
}
