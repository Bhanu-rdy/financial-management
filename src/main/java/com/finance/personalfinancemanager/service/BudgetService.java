package com.finance.personalfinancemanager.service;

import com.finance.personalfinancemanager.dto.budget_dto.BudgetRequest;
import com.finance.personalfinancemanager.dto.budget_dto.BudgetResponse;
import com.finance.personalfinancemanager.entity.Budget;
import com.finance.personalfinancemanager.entity.Category;
import com.finance.personalfinancemanager.entity.Transaction;
import com.finance.personalfinancemanager.entity.User;
import com.finance.personalfinancemanager.enums.CategoryType;
import com.finance.personalfinancemanager.repository.BudgetRepository;
import com.finance.personalfinancemanager.repository.CategoryRepository;
import com.finance.personalfinancemanager.repository.TransactionRepository;
import com.finance.personalfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // Create budget
    public BudgetResponse createBudget(Long userId, BudgetRequest request) {

        //Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Find category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        //Verify category belong to user
        if(!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Category does not belong to user");
        }

        //Only expense categories can have budgets
        if(category.getType() != CategoryType.EXPENSE) {
            throw new RuntimeException("Budgets can only be set for expense categories");
        }

        //Check if budget already exits for this category/month/year
        if(budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(userId, request.getCategoryId(), request.getMonth(), request.getYear())) {
            throw new RuntimeException("Budget already exists for this category and month");
        }

        //Validate month (1-12)
        if(request.getMonth() < 1 || request.getMonth() > 12) {
            throw new RuntimeException("Invalid month. Must be between 1 and 12");
        }

        //Validate alert threshold
        if(request.getAlertThreshold() < 1 || request.getAlertThreshold() > 100) {
            throw new RuntimeException("Alert threshold must be between 1 and 100");
        }

        //Create budget
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());
        budget.setAlertThreshold(request.getAlertThreshold());
        budget.setAlertTriggered(false);

        //Save to database
        Budget savedBudget = budgetRepository.save(budget);

        //Calculate spent amount
        BigDecimal spentAmount = calculateSpentAmount(userId, request.getCategoryId(), request.getMonth(), request.getYear());

        //Calculate percentage
        int percentage = calculatePercentage(spentAmount, savedBudget.getAmount());

        //Check if alert should be triggered
        if(percentage >= savedBudget.getAlertThreshold() && !savedBudget.getAlertTriggered()) {
            savedBudget.setAlertTriggered(true);
            budgetRepository.save(savedBudget);
        }

        //Return response
        return new BudgetResponse(
                savedBudget.getId(),
                savedBudget.getCategory().getId(),
                savedBudget.getCategory().getName(),
                savedBudget.getAmount(),
                spentAmount,
                percentage,
                savedBudget.getMonth(),
                savedBudget.getYear(),
                savedBudget.getAlertThreshold(),
                savedBudget.getAlertTriggered()
        );
    }

    //Get all budgets for user
    public List<BudgetResponse> getAllBudgets(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        List<BudgetResponse> responseList = new ArrayList<>();

        for(Budget budget : budgets) {
            BigDecimal spentAmount = calculateSpentAmount(userId, budget.getCategory().getId(), budget.getMonth(), budget.getYear());

            int percentage = calculatePercentage(spentAmount, budget.getAmount());

            //Update alert status
            if(percentage >= budget.getAlertThreshold() && !budget.getAlertTriggered()) {
                budget.setAlertTriggered(true);
                budgetRepository.save(budget);
            }

            BudgetResponse response = new BudgetResponse(
                    budget.getId(),
                    budget.getCategory().getId(),
                    budget.getCategory().getName(),
                    budget.getAmount(),
                    spentAmount,
                    percentage,
                    budget.getMonth(),
                    budget.getYear(),
                    budget.getAlertThreshold(),
                    budget.getAlertTriggered()
            );
            responseList.add(response);
        }

        return responseList;
    }

    //Get budgets for specific month/year
    public List<BudgetResponse> getBudgetsByMonth(Long userId, Integer month, Integer year) {
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
        List<BudgetResponse> responseList = new ArrayList<>();

        for (Budget budget : budgets) {
            BigDecimal spentAmount = calculateSpentAmount(userId, budget.getCategory().getId(),
                    month, year);
            int percentage = calculatePercentage(spentAmount, budget.getAmount());

            BudgetResponse response = new BudgetResponse(
                    budget.getId(),
                    budget.getCategory().getId(),
                    budget.getCategory().getName(),
                    budget.getAmount(),
                    spentAmount,
                    percentage,
                    budget.getMonth(),
                    budget.getYear(),
                    budget.getAlertThreshold(),
                    budget.getAlertTriggered()
            );
            responseList.add(response);
        }

        return responseList;

    }

    //Calculate spent amount for category in specific month
    private BigDecimal calculateSpentAmount(Long userId, Long categoryId, Integer month, Integer year) {
        //Get first and last day of month
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        //Get transactions for this category in this month
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate);

        //Sum up expenses for this category
        BigDecimal total = BigDecimal.ZERO;

        for(Transaction transaction : transactions) {
            //Check if transaction belongs to this category
            if(transaction.getCategory().getId().equals(categoryId)) {
                //Check if transaction is expense type
                if(transaction.getType() == CategoryType.EXPENSE) {
                    //Add amount to total
                    total = total.add(transaction.getAmount());
                }
            }
        }
        return total;
    }

    //Calculate percentage of budget used
    private int calculatePercentage(BigDecimal spent, BigDecimal budget) {
        if(budget.compareTo(BigDecimal.ZERO) == 0) return 0;

        BigDecimal percentage = spent
                .multiply(BigDecimal.valueOf(100))
                .divide(budget, 0, RoundingMode.HALF_UP);

        return percentage.intValue();
    }

}
