package com.authserver.controller;

import com.authserver.dto.UserRequestDTO;
import com.authserver.exceptions.InvalidEmailException;
import com.authserver.exceptions.UserExistException;
import com.authserver.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

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
            checkUserRequestDTONotNull(userRequestDTO);
            checkAtInEmail(userRequestDTO.getEmail());
            userService.saveUser(userRequestDTO);
            return "redirect:/login";
        } catch (UserExistException e) {
            model.addAttribute("error", "Error caused by existing user: " + e.getMessage());
            return "registration";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Error caused by input: " + e.getMessage());
            return "registration";
        } catch (InvalidEmailException e) {
            model.addAttribute("error", e.getMessage());
            return "registration";
        }
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

    private void checkAtInEmail(String email) {
        if (!email.contains("@") || email.indexOf("@") != email.lastIndexOf("@")) {
            throw new InvalidEmailException("E-mail has invalid form");
        }
        int indexOfAt = email.indexOf("@");
        String emailAfterAt = email.substring(indexOfAt);
        if (!emailAfterAt.contains(".") || emailAfterAt.indexOf(".") != emailAfterAt.lastIndexOf(".")) {
            throw new InvalidEmailException("E-mail has invalid form");
        }
    }

}
