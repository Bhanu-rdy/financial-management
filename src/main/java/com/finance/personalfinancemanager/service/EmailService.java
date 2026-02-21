package com.finance.personalfinancemanager.service;


import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
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
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("Finance Manager <bhanuprakashreddyvangala@gmail.com>");
            helper.setTo(toEmail);
            helper.setSubject("Finance Manager: Email Verification");
            helper.setText("Your OTP code is: " + otp + "\n\nThis code will expire in 5 minutes.\n\nIf you didn't request this, please ignore this email.");

            mailSender.send(message);
        }
        catch (Exception e) {
            throw new RuntimeException(toEmail);
        }
    }

}
