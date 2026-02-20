package com.finance.personalfinancemanager.service;

import com.finance.personalfinancemanager.dto.transaction_dto.TransactionRequest;
import com.finance.personalfinancemanager.dto.transaction_dto.TransactionResponse;
import com.finance.personalfinancemanager.entity.Category;
import com.finance.personalfinancemanager.entity.Transaction;
import com.finance.personalfinancemanager.entity.User;
import com.finance.personalfinancemanager.enums.CategoryType;
import com.finance.personalfinancemanager.repository.CategoryRepository;
import com.finance.personalfinancemanager.repository.TransactionRepository;
import com.finance.personalfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    //Create transaction
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {

        //Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Find category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        //Verify category belongs to user
        if(!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Category does not belong to user");
        }

        //Verify category type matches transaction type
        if(!category.getType().equals(request.getType())) {
            throw new RuntimeException("Category type does not match transaction type");
        }

        //Create transaction
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());

        //Save to database
        Transaction savedTransaction = transactionRepository.save(transaction);

        return new TransactionResponse(
                savedTransaction.getId(),
                savedTransaction.getCategory().getId(),
                savedTransaction.getCategory().getName(),
                savedTransaction.getAmount(),
                savedTransaction.getType(),
                savedTransaction.getDescription(),
                savedTransaction.getTransactionDate(),
                savedTransaction.getCreatedAt().toString()
        );
    }

    //Get all transaction for user
    public List<TransactionResponse> getAllTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
        List<TransactionResponse> responsesList = new ArrayList<>();

        for(Transaction transaction : transactions) {
            TransactionResponse response = new TransactionResponse(
                    transaction.getId(),
                    transaction.getCategory().getId(),
                    transaction.getCategory().getName(),
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getDescription(),
                    transaction.getTransactionDate(),
                    transaction.getCreatedAt().toString()
            );
            responsesList.add(response);
        }
        return responsesList;
    }

    // Get transactions by type
    public List<TransactionResponse> getTransactionsByType(Long userId, CategoryType type) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndTypeOrderByTransactionDateDesc(userId, type);
        List<TransactionResponse> responseList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionResponse response = new TransactionResponse(
                    transaction.getId(),
                    transaction.getCategory().getId(),
                    transaction.getCategory().getName(),
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getDescription(),
                    transaction.getTransactionDate(),
                    transaction.getCreatedAt().toString()
            );
            responseList.add(response);
        }

        return responseList;
    }

    // Get transactions by date range
    public List<TransactionResponse> getTransactionsByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        List<TransactionResponse> responseList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionResponse response = new TransactionResponse(
                    transaction.getId(),
                    transaction.getCategory().getId(),
                    transaction.getCategory().getName(),
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getDescription(),
                    transaction.getTransactionDate(),
                    transaction.getCreatedAt().toString()
            );
            responseList.add(response);
        }

        return responseList;
    }
}
