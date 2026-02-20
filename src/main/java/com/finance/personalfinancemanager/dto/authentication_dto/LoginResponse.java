package com.finance.personalfinancemanager.dto.authentication_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String message;
    private Long userId;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String token;
}