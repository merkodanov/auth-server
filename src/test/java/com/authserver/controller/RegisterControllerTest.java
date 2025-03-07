package com.authserver.controller;

import com.authserver.dto.UserRequestDTO;
import com.authserver.exceptions.UserExistException;
import com.authserver.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;


@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {

    private UserRequestDTO userRequestDTO;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void initUser() {
        userRequestDTO = new UserRequestDTO("Vladek",
                "email@gmail.com",
                "password");
    }

    static Stream<UserRequestDTO> userRequestDTOStream() {
        return Stream.of(
                new UserRequestDTO("fill", "fill", null),
                new UserRequestDTO("fill", null, "fill"),
                new UserRequestDTO(null, "fill", "fill")
        );
    }

    @Test
    void load_registration_form_is_success() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andReturn();

        Assertions.assertNotNull(result.getModelAndView());
        Assertions.assertEquals("registration", result.getModelAndView().getViewName());
    }

    @Test
    void register_User_is_success() throws Exception {
        Mockito.when(userService.saveUser(Mockito.any(UserRequestDTO.class))).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .param("username", userRequestDTO.getUsername())
                        .param("email", userRequestDTO.getEmail())
                        .param("password", userRequestDTO.getPassword()))
                .andReturn();

        Assertions.assertNotNull(result.getModelAndView());
        Assertions.assertEquals("redirect:/login", result.getModelAndView().getViewName());
        Mockito.verify(userService, Mockito.times(1)).saveUser(Mockito.any(UserRequestDTO.class));
    }

    @Test
    void register_User_fails_when_User_is_exists() throws Exception {
        UserExistException userExistException = Mockito.mock(UserExistException.class);
        Mockito.when(userService.saveUser(userRequestDTO)).thenThrow(userExistException);
        Mockito.when(userExistException.getMessage()).thenReturn("User exists");

        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .param("username", userRequestDTO.getUsername())
                        .param("email", userRequestDTO.getEmail())
                        .param("password", userRequestDTO.getPassword()))
                .andExpect(model().attribute("error", "Error caused by existing user: " + userExistException.getMessage()));
    }

    @Test
    void register_User_fails_when_fields_are_null() throws Exception {
        IllegalArgumentException illegalArgumentException = Mockito.mock(IllegalArgumentException.class);
        Mockito.when(userService.saveUser(userRequestDTO)).thenThrow(illegalArgumentException);
        Mockito.when(illegalArgumentException.getMessage()).thenReturn("Illegal argument");

        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .param("username", userRequestDTO.getUsername())
                        .param("email", userRequestDTO.getEmail())
                        .param("password", userRequestDTO.getPassword()))
                .andExpect(model().attribute("error", "Error caused by input: " + illegalArgumentException.getMessage()));
    }

    @Test
    void saving_user_when_UserRequestDTO_is_null() throws Exception {
        userRequestDTO = null;

        mockMvc.perform(MockMvcRequestBuilders.post("/signup"))
                .andExpect(model().attributeExists("error"));
    }

    @ParameterizedTest
    @MethodSource("userRequestDTOStream")
    void saving_user_when_UserRequestDTO_fields_are_null(UserRequestDTO userRequestDTO) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .param("username", userRequestDTO.getUsername())
                        .param("email", userRequestDTO.getEmail())
                        .param("password", userRequestDTO.getPassword()))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    void saving_user_with_invalid_email() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO("Vlad",
                "email.com", "123");

        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .param("username", userRequestDTO.getUsername())
                        .param("email", userRequestDTO.getEmail())
                        .param("password", userRequestDTO.getPassword()))
                .andExpect(model().attributeExists("error"));
    }
}
