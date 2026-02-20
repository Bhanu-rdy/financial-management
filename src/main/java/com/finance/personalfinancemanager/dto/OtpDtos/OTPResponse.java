package com.finance.personalfinancemanager.dto.OtpDtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPResponse {
    private String message;
    private boolean success;
}
