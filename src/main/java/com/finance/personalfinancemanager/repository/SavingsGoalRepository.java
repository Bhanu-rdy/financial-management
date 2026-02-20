package com.finance.personalfinancemanager.repository;

import com.finance.personalfinancemanager.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    // Get all savings goals for user
    List<SavingsGoal> findByUserIdOrderByDeadlineAsc(Long userId);

    // Get active (not completed) goals
    List<SavingsGoal> findByUserIdAndCompletedFalseOrderByDeadlineAsc(Long userId);

    // Get completed goals
    List<SavingsGoal> findByUserIdAndCompletedTrueOrderByDeadlineDesc(Long userId);

}
