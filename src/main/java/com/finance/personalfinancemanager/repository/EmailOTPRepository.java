package com.finance.personalfinancemanager.repository;

import com.finance.personalfinancemanager.entity.EmailOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOTPRepository extends JpaRepository<EmailOTP, Long> {

    Optional<EmailOTP> findByEmailAndOtpAndVerifiedFalse(String email, String otp);

    Optional<EmailOTP> findFirstByEmailOrderByCreatedAtDesc(String email);
}
