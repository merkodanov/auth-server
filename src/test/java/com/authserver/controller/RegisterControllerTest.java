package com.authserver.controller;

import com.authserver.dto.UserRequestDTO;
import com.authserver.exceptions.UserExistException;
import com.authserver.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
        userRequestDTO = new UserRequestDTO("Vladd",
                "email@gmail.com",
                "password");
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
}
