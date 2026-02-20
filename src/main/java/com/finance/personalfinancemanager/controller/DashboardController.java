package com.finance.personalfinancemanager.controller;

import com.finance.personalfinancemanager.dto.dashboard_dto.DashboardStatsResponse;
import com.finance.personalfinancemanager.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats(HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");

            if(userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized"));

            DashboardStatsResponse stats = dashboardService.getDashboardStats(userId);
            return ResponseEntity.status(HttpStatus.OK).body(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }
}
