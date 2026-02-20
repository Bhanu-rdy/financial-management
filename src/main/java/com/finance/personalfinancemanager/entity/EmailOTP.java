package com.finance.personalfinancemanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_otps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailOTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false, columnDefinition = "datetime")
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "datetime")
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean verified = false;

}

