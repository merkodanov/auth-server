package com.authserver.controller;

import com.authserver.dto.UserRequestDTO;
import com.authserver.exceptions.UserExistException;
import com.authserver.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signup")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String registerUser() {
        return "registration";
    }

    @PostMapping
    public String registerUser(@ModelAttribute UserRequestDTO userRequestDTO,
                               Model model) {
        try {
            userService.saveUser(userRequestDTO);
            return "redirect:/login";
        } catch (UserExistException e) {
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
    }
}
