package com.finance.personalfinancemanager.dto.OtpDtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOTPRequest {
    private String email;
}
