
package com.finance.personalfinancemanager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.security.SecureRandom;
import java.util.Map;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    @Value("${RESEND_FROM_EMAIL}")
    private String fromEmail;

    private final RestClient restClient;

    public EmailService(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.resend.com")
                .build();
    }

    // Generates 6 digit OTP
    public String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOTPEmail(String toEmail, String otp) {

        try {

            String htmlContent = """
                    <!DOCTYPE html>
                    <html>
                    <body style="font-family: Arial, sans-serif;
                                 background-color: #f4f6f8;
                                 padding: 30px;">

                        <div style="max-width: 500px;
                                    margin: auto;
                                    background: white;
                                    padding: 30px;
                                    border-radius: 8px;">

                            <h2 style="color: #17365D;
                                       text-align: center;">
                                Personal Finance Manager
                            </h2>

                            <h3>Hello!</h3>

                            <p>
                                You requested an OTP to verify
                                your email address.
                            </p>

                            <h1 style="text-align: center;
                                       letter-spacing: 8px;
                                       color: #17365D;">
                                %s
                            </h1>

                            <p>This OTP is valid for 5 minutes.</p>

                            <p>
                                Please do not share this OTP
                                with anyone.
                            </p>

                            <p>
                                If you didn't request this OTP,
                                please ignore this email.
                            </p>

                            <p>
                                Thank you,<br>
                                <strong>
                                    Personal Finance Manager Team
                                </strong>
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(otp);

            Map<String, Object> requestBody = Map.of(
                    "from", "Finance Manager <" + fromEmail + ">",
                    "to", new String[]{toEmail},
                    "subject", "Finance Manager - Email Verification OTP",
                    "html", htmlContent
            );

            String response = restClient.post()
                    .uri("/emails")
                    .header(HttpHeaders.AUTHORIZATION,
                            "Bearer " + resendApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            System.out.println("Email sent successfully via Resend!");
            System.out.println("Resend response: " + response);

        } catch (Exception e) {

            System.err.println("Resend email error: " + e.getMessage());

            throw new RuntimeException(
                    "Failed to send OTP email", e);
        }
    }
}
