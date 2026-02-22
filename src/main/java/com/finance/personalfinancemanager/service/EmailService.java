package com.finance.personalfinancemanager.service;

import com.sendgrid.*;
it import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;

@Service
public class EmailService {

    @Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey;

    // Generates 6 digit OTP
    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOTPEmail(String toEmail, String otp) {
        try {
            System.out.println("=== SENDING EMAIL VIA SENDGRID REST API ===");
            System.out.println("To: " + toEmail);
            System.out.println("OTP: " + otp);
            System.out.println("API Key starts with: " + (sendGridApiKey != null ? sendGridApiKey.substring(0, 5) : "NULL"));

            // Create email using SendGrid REST API
            Email from = new Email("bhanuprakashreddyvangala@gmail.com", "Finance Manager");
            Email to = new Email(toEmail);
            String subject = "Finance Manager: Email Verification";

            String textContent = "Your OTP code is: " + otp + "\n\n" +
                    "This code will expire in 5 minutes.\n\n" +
                    "If you didn't request this, please ignore this email.";

            Content content = new Content("text/plain", textContent);
            Mail mail = new Mail(from, subject, to, content);

            // Send via SendGrid REST API
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("SendGrid Response Status: " + response.getStatusCode());
            System.out.println("SendGrid Response Body: " + response.getBody());
            System.out.println("Email sent successfully!");
            System.out.println("==========================================");

        } catch (IOException e) {
            System.err.println("Email error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}