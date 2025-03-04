package com.authserver.service;

import com.authserver.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public String getUser(String vlad){
        return "VLAD_USER";
    }

    public boolean saveUser(User user) {
        return true;
    }
}
