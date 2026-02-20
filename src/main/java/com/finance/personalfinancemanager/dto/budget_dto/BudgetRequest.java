package com.finance.personalfinancemanager.dto.budget_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequest {
    private Long categoryId;
    private BigDecimal amount;
    private Integer month;      //1-12
    private Integer year;
    private Integer alertThreshold;  //80, 90, 100
}
