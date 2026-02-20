package com.finance.personalfinancemanager.service;

import com.finance.personalfinancemanager.entity.EmailOTP;
import com.finance.personalfinancemanager.repository.EmailOTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OTPService {

    @Autowired
    private EmailOTPRepository emailOTPRepository;

    @Autowired
    private EmailService emailService;

    // Send OTP to user's email
    public String sendOtp(String email) {

        //Generate 6-digit OTP
        String otp = emailService.generateOTP();
        System.out.println(otp);
        //Create OTP record
        EmailOTP emailOTP = new EmailOTP();
        emailOTP.setEmail(email);
        emailOTP.setOtp(otp);
        emailOTP.setCreatedAt(LocalDateTime.now());
        emailOTP.setExpiresAt(LocalDateTime.now().plusMinutes(5)); //expires after 5 minutes

        //save to database
        emailOTPRepository.save(emailOTP);

        //send email
        emailService.sendOTPEmail(email, otp);

        return "OTP sent to " + email;
    }

    //Verify OTP
    public boolean verifyOTP(String email, String otp) {

        //Find OTP record
        Optional<EmailOTP> otpRecord = emailOTPRepository.findByEmailAndOtpAndVerifiedFalse(email, otp);

        if(!otpRecord.isPresent()) {
            return false; // OTP not found or already verified
        }

        EmailOTP emailOTP = otpRecord.get();

        //check if expire
        if(LocalDateTime.now().isAfter(emailOTP.getExpiresAt())) {
            return false; //OTP expired
        }

        //Mark as verified
        emailOTP.setVerified(true);
        emailOTPRepository.save(emailOTP);
        return true;
    }
}
