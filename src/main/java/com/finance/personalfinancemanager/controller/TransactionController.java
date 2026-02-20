package com.finance.personalfinancemanager.controller;

import com.finance.personalfinancemanager.dto.transaction_dto.TransactionRequest;
import com.finance.personalfinancemanager.dto.transaction_dto.TransactionResponse;
import com.finance.personalfinancemanager.entity.Transaction;
import com.finance.personalfinancemanager.enums.CategoryType;
import com.finance.personalfinancemanager.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    //Create transaction (Protected requires JWT)
    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequest request, HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");

            if(userId == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }
            TransactionResponse response = transactionService.createTransaction(userId, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    //Get all transactions (Protected - requires JWT)
    @GetMapping
    public ResponseEntity<?> getAllTransactions(HttpServletRequest httpServletRequest) {
        try {
            Long userId = (Long) httpServletRequest.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }

            List<TransactionResponse> transactions = transactionService.getAllTransactions(userId);
            return ResponseEntity.status(HttpStatus.OK).body(transactions);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

        // Get transactions by type (Protected - requires JWT)
        @GetMapping("/type/{type}")
        public ResponseEntity<?> getTransactionsByType(@PathVariable CategoryType type, HttpServletRequest httpRequest) {
            try {
                Long userId = (Long) httpRequest.getAttribute("userId");

                if (userId == null) {
                    return ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(new ErrorResponse("Unauthorized"));
                }

                List<TransactionResponse> transactions = transactionService.getTransactionsByType(userId, type);
                return ResponseEntity.ok(transactions);

            } catch (Exception e) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse(e.getMessage()));
            }
        }

    // Get transactions by date range (Protected - requires JWT)
    @GetMapping("/range")
    public ResponseEntity<?> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");

            if (userId == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized"));
            }

            List<TransactionResponse> transactions = transactionService.getTransactionsByDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(transactions);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

}
