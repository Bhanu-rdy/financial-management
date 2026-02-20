package com.finance.personalfinancemanager.repository;

import com.finance.personalfinancemanager.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    // Get all budgets for user
    List<Budget> findByUserId(Long id);

    // Get budgets for specific month/year
    List <Budget> findByUserIdAndMonthAndYear(Long userId, Integer month, Integer year);

    //Get budget for specific category and month
    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, Integer month, Integer year);

    //Check if budget exists
    boolean existsByUserIdAndCategoryIdAndMonthAndYear(
            Long userId, Long categoryId, Integer month, Integer year
    );
}
