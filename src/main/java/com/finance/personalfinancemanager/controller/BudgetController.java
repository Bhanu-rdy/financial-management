package com.finance.personalfinancemanager.controller;

import com.finance.personalfinancemanager.dto.budget_dto.BudgetRequest;
import com.finance.personalfinancemanager.dto.budget_dto.BudgetResponse;
import com.finance.personalfinancemanager.service.BudgetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    //Create budget (Protected - requires JWT)
    @PostMapping
    public ResponseEntity<?> createBudget(@RequestBody BudgetRequest request, HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");

            if(userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized"));

            BudgetResponse response = budgetService.createBudget(userId, request);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    //Get all budgets (Protected - requires JWT)
    @GetMapping
    public ResponseEntity<?> getAllBudgets(HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");

            if(userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized"));

            List<BudgetResponse> budgets = budgetService.getAllBudgets(userId);
            return ResponseEntity.status(HttpStatus.OK).body(budgets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }

    //Get budgets by month (Protected - requires JWT)
    @GetMapping("/month/{month}/year/{year}")
    public ResponseEntity<?> getBudgetsByMonth(@PathVariable Integer month, @PathVariable Integer year, HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");

            if(userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized"));

            List<BudgetResponse> budgets = budgetService.getBudgetsByMonth(userId, month, year);
            return ResponseEntity.status(HttpStatus.OK).body(budgets);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
        }
    }
}
