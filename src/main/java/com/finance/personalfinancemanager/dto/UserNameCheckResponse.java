package com.finance.personalfinancemanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNameCheckResponse {
    private boolean available;
    private String message;
}
