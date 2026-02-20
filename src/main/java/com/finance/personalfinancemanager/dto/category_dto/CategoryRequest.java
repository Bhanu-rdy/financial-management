package com.finance.personalfinancemanager.dto.category_dto;

import com.finance.personalfinancemanager.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CategoryRequest {
    private String name;
    private CategoryType type;  // ENUM: INCOME or EXPENSE
}