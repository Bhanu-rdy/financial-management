package com.finance.personalfinancemanager.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //hash a plain text password
    public String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    //check if plain password matches hashed password
    public boolean checkPassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}
