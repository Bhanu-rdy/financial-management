package com.finance.personalfinancemanager.dto.email_check_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailCheckResponse {
    private boolean available;
    private String message;
}
