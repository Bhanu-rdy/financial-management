package com.finance.personalfinancemanager.dto.email_check_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailCheckRequest {
    private String email;
}
