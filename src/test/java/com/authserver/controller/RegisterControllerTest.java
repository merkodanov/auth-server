package com.authserver.controller;

import com.authserver.model.User;
import com.authserver.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@WebMvcTest(RegisterController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void getRegistrationForm_load_success() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/register"))
                .andReturn();

        Assertions.assertNotNull(result.getModelAndView());
        Assertions.assertEquals("registration", result.getModelAndView().getViewName());
    }

    @Test
    void registerUser_is_success() throws Exception {
        User user = new User("Vlad", "password", "vladislavik@gmail.com", "ROLE_USER");
        Mockito.when(userService.saveUser(Mockito.any(User.class))).thenReturn(true);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .param("username", user.getUsername())
                        .param("email", user.getEmail())
                        .param("password", user.getPassword()))
                .andReturn();

        Assertions.assertNotNull(result.getModelAndView());
        Assertions.assertEquals("registration-success", result.getModelAndView().getViewName());
        Mockito.verify(userService, Mockito.times(1)).saveUser(Mockito.any(User.class));
    }
}
