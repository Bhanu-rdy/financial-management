package com.finance.personalfinancemanager.dto.budget_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private BigDecimal spentAmount;
    private Integer percentage;           // % of budget used
    private Integer month;
    private Integer year;
    private Integer alertThreshold;
    private Boolean alertTriggered;
}
