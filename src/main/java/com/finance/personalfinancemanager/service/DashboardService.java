package com.finance.personalfinancemanager.service;

import com.finance.personalfinancemanager.dto.dashboard_dto.DashboardStatsResponse;
import com.finance.personalfinancemanager.entity.SavingsGoal;
import com.finance.personalfinancemanager.entity.Transaction;
import com.finance.personalfinancemanager.enums.CategoryType;
import com.finance.personalfinancemanager.repository.SavingsGoalRepository;
import com.finance.personalfinancemanager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    public DashboardStatsResponse getDashboardStats(Long userId) {

        //Get all transactions for user
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);

        //Calculate total income
        BigDecimal totalIncome = BigDecimal.ZERO;
        for(Transaction transaction : transactions) {
            if(transaction.getType() == CategoryType.INCOME) totalIncome = totalIncome.add(transaction.getAmount());
        }

        //Calculate total expenses
        BigDecimal totalExpense = BigDecimal.ZERO;
        for(Transaction transaction : transactions) {
            if(transaction.getType() == CategoryType.EXPENSE) totalExpense = totalExpense.add(transaction.getAmount());
        }

        //Calculate net balance
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        //Count savings goals
        List<SavingsGoal> goals = savingsGoalRepository.findByUserIdOrderByDeadlineAsc(userId);
        int totalGoals = goals.size();

        return new DashboardStatsResponse(
                totalIncome,
                totalExpense,
                netBalance,
                totalGoals
        );
    }
}
