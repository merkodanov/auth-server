package com.authserver.controller;

import com.authserver.model.User;
import com.authserver.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {
    final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.saveUser(user);

        return "registration-success";
    }
}
