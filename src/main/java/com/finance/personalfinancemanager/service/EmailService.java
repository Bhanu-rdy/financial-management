package com.finance.personalfinancemanager.service;


import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    //generates 6 digit otp
    public  String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOTPEmail(String toEmail,String otp) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("Finance Manager <bhanuprakashreddyvangala@gmail.com>");
            mailMessage.setTo(toEmail);
            mailMessage.setSubject("Finance Manager: Email Verification");
            mailMessage.setText("Your OTP code is: " + otp + "\n\nThis code will expire in 5 minutes.\n\nIf you didn't request this, please ignore this email.");

            mailSender.send(mailMessage);
        }
        catch (Exception e) {
            throw new RuntimeException(toEmail);
        }
    }

}
