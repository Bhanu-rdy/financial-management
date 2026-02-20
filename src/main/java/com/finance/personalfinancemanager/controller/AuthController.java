package com.finance.personalfinancemanager.controller;

import com.finance.personalfinancemanager.dto.UserNameCheckRequest;
import com.finance.personalfinancemanager.dto.UserNameCheckResponse;
import com.finance.personalfinancemanager.dto.authentication_dto.*;
import com.finance.personalfinancemanager.dto.email_check_dto.EmailCheckRequest;
import com.finance.personalfinancemanager.dto.email_check_dto.EmailCheckResponse;
import com.finance.personalfinancemanager.repository.UserRepository;
import com.finance.personalfinancemanager.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    //Register endpoint
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.registerUser(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    //Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletResponse response) {
        try {
            LoginResponse loginResponse = authService.loginUser(request);

            //Create httpOnly cookie with JWT token
            Cookie cookie = new Cookie("token", loginResponse.getToken());
            cookie.setHttpOnly(true); //cannot be accessed by javascript
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60); //24 hours
            response.addCookie(cookie);

            response.addHeader("Set-Cookie",
                    String.format("token=%s; Path=/; Max-Age=86400; SameSite=Lax",
                            loginResponse.getToken()));

            return ResponseEntity.status(HttpStatus.OK).body(loginResponse);

        } catch (RuntimeException e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    //logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        //clear cookie
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse("Logged out successfully"));
    }

    //Check userName availability
    @PostMapping("/check-username")
    public ResponseEntity<?> checkUserName(@RequestBody UserNameCheckRequest request) {
        try {
            UserNameCheckResponse response = authService.checkUserName(request.getUserName());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to check Username"));
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestBody EmailCheckRequest request) {
        try {
            EmailCheckResponse response = authService.checkEmail(request.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Failed to check email"));
        }
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class ErrorResponse {

    private String error;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class MessageResponse {
    private String message;

}
