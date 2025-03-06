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
    void initUser(){
        userRequestDTO = new UserRequestDTO("Vladd",
                "email@gmail.com",
                "password");
    }

    @Test
    void getRegistrationForm_load_success() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andReturn();

        Assertions.assertNotNull(result.getModelAndView());
        Assertions.assertEquals("registration", result.getModelAndView().getViewName());
    }

    @Test
    void registerUser_is_success() throws Exception {
        Mockito.when(userService.saveUser(Mockito.any(UserRequestDTO.class))).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .param("username", userRequestDTO.getUsername())
                        .param("email", userRequestDTO.getEmail())
                        .param("password", userRequestDTO.getPassword()))
                .andReturn();

        Assertions.assertNotNull(result.getModelAndView());
        Assertions.assertEquals("registration-success", result.getModelAndView().getViewName());
        Mockito.verify(userService, Mockito.times(1)).saveUser(Mockito.any(UserRequestDTO.class));
    }

    @Test
    void registerUser_fails_when_User_is_exists() throws Exception{
        Mockito.when(userService.saveUser(userRequestDTO)).thenThrow(UserExistException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                .param("username", userRequestDTO.getUsername())
                .param("email", userRequestDTO.getEmail())
                .param("password", userRequestDTO.getPassword()))
                .andExpect(model().attribute("error","User is exists!"));
    }
}
