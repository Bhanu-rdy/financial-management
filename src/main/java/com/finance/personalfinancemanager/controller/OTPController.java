package com.finance.personalfinancemanager.controller;

import com.finance.personalfinancemanager.dto.OtpDtos.OTPResponse;
import com.finance.personalfinancemanager.dto.OtpDtos.VerifyOTPRequest;
import com.finance.personalfinancemanager.dto.authentication_dto.RegisterRequest;
import com.finance.personalfinancemanager.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/otp")
public class OTPController {

    @Autowired
    private OTPService otpService;

    //Send OTP to email
    @PostMapping("/send")
    public ResponseEntity<?> sendOTP(@RequestBody RegisterRequest request) {
        try {
            String message = otpService.sendOtp(request.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(new OTPResponse(message, true));
        }
        catch (Exception e) {
            System.out.println("error sending email");
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new OTPResponse("Failed to send OTP to: " + e.getMessage(), false));
        }
    }

    //Verify OTP
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@RequestBody VerifyOTPRequest request) {
        try {
            boolean isValid = otpService.verifyOTP(request.getEmail(), request.getOtp());
            if(isValid) {
                return ResponseEntity.status(HttpStatus.OK).body(new OTPResponse("OTP verified successfully", true));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new OTPResponse("Invalid or expired OTP", false));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new OTPResponse("Error verifying OTP", false));
        }
    }
}
