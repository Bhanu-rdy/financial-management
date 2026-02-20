package com.finance.personalfinancemanager.controller;

import com.finance.personalfinancemanager.dto.savings_goal_dto.SavingsGoalRequest;
import com.finance.personalfinancemanager.dto.savings_goal_dto.SavingsGoalResponse;
import com.finance.personalfinancemanager.service.SavingsGoalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/savings")
public class SavingsGoalController {

    @Autowired
    private SavingsGoalService savingsGoalService;

    //Create savings goal (Protected - requires JWT)
    @PostMapping
    public ResponseEntity<?> createSavingsGoal(@RequestBody SavingsGoalRequest request, HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");

            if(userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized"));

            SavingsGoalResponse response = savingsGoalService.createSavingsGoal(userId, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    // Get all savings goals (Protected - requires JWT)
    @GetMapping
    public ResponseEntity<?> getAllSavingsGoals(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }

            List<SavingsGoalResponse> goals = savingsGoalService.getAllSavingsGoals(userId);
            return ResponseEntity.status(HttpStatus.OK).body(goals);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    // Update savings amount (Protected - requires JWT)
    @PutMapping("/{goalId}/amount")
    public ResponseEntity<?> updateSavingsAmount(
            @PathVariable Long goalId,
            @RequestParam BigDecimal amount,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }

            SavingsGoalResponse response = savingsGoalService.updateSavingsAmount(userId, goalId, amount);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

}
