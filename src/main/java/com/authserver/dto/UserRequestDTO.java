package com.authserver.dto;

import lombok.Value;

@Value
public class UserRequestDTO {
    String username;
    String email;
    String password;
}
