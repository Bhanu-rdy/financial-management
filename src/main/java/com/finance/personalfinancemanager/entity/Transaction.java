package com.finance.personalfinancemanager.entity;

import com.finance.personalfinancemanager.enums.CategoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private CategoryType type; // INCOME OR EXPENSE

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(updatable = false, columnDefinition = "datetime")
    private LocalDateTime createdAt = LocalDateTime.now();
}
