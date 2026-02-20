package com.finance.personalfinancemanager.dto.transaction_dto;

import com.finance.personalfinancemanager.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private CategoryType type;
    private String description;
    private LocalDate transactionDate;
    private String createdAt;
}
