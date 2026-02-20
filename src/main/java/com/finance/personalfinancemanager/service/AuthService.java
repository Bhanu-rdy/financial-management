package com.finance.personalfinancemanager.service;


import com.finance.personalfinancemanager.dto.UserNameCheckResponse;
import com.finance.personalfinancemanager.dto.authentication_dto.LoginRequest;
import com.finance.personalfinancemanager.dto.authentication_dto.LoginResponse;
import com.finance.personalfinancemanager.dto.authentication_dto.RegisterRequest;
import com.finance.personalfinancemanager.dto.authentication_dto.RegisterResponse;
import com.finance.personalfinancemanager.dto.email_check_dto.EmailCheckResponse;
import com.finance.personalfinancemanager.entity.User;
import com.finance.personalfinancemanager.repository.UserRepository;
import com.finance.personalfinancemanager.util.JwtUtil;
import com.finance.personalfinancemanager.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordUtil passwordUtil;

    //Check if username is available
    public UserNameCheckResponse checkUserName(String userName) {
        boolean exists = userRepository.existsByUserName(userName);

        if(exists) {
            return new UserNameCheckResponse(false, "Username is not available");
        }
        else {
            return new UserNameCheckResponse(true, "Username is available");
        }
    }

    //Check email availability
    public EmailCheckResponse checkEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);

        if(exists) {
            return new EmailCheckResponse(false, "Email is already registered");

        }
        else {
            return new EmailCheckResponse(true, "Email is available");
        }
    }

    public RegisterResponse registerUser(RegisterRequest request) {

        // Check if userName already exists
        if(userRepository.existsByUserName(request.getUserName())) {
            throw new RuntimeException("User name already exists");
        }

        //check if email already exists
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        //check if the password is atleast 8 characters
        if(request.getPassword().length() < 8) {
            throw new RuntimeException("password must be atleast 8 characters");
        }

        //create new user
        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());

        //hash the password and store
        String hashedPassword = passwordUtil.hashPassword(request.getPassword());
        user.setPassword(hashedPassword);
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        //save user to database
        User savedUser = userRepository.save(user);

        //return response
        return new RegisterResponse(
                "User registered successfully",
                savedUser.getId(),
                savedUser.getUserName()
        );
    }

    public LoginResponse loginUser(LoginRequest request) {
        //find user by username
        Optional<User> userOptional = userRepository.findByUserName(request.getUserName());

        if(!userOptional.isPresent()) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOptional.get();

        //check if the password matches
        boolean passwordMatches = passwordUtil.checkPassword(request.getPassword(), user.getPassword());

        if(!passwordMatches) throw new RuntimeException("Invalid password");

        //Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUserName());
        //return user details (login successful)
        return new LoginResponse(
                "Login successful",
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                token
        );
    }



}
