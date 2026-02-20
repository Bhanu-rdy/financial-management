package com.finance.personalfinancemanager.service;

import com.finance.personalfinancemanager.dto.savings_goal_dto.SavingsGoalRequest;
import com.finance.personalfinancemanager.dto.savings_goal_dto.SavingsGoalResponse;
import com.finance.personalfinancemanager.entity.SavingsGoal;
import com.finance.personalfinancemanager.entity.User;
import com.finance.personalfinancemanager.repository.SavingsGoalRepository;
import com.finance.personalfinancemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class SavingsGoalService {

    @Autowired
    private SavingsGoalRepository savingsGoalRepository;

    @Autowired
    private UserRepository userRepository;

    //Create savings goal
    public SavingsGoalResponse createSavingsGoal(Long userId, SavingsGoalRequest request) {

        //Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //Validate target amount
        if(request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Target amount must be greater than zero");
        }

        if(request.getCurrentAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Target amount must be greater than zero");
        }

        if(request.getCurrentAmount().compareTo(request.getTargetAmount()) > 0) {
            throw new RuntimeException("Current amount cannot exceed target amount");
        }

        if (request.getDeadline().isBefore(LocalDate.now())) {
            throw new RuntimeException("Deadline cannot be in the past");
        }

        // Create savings goal
        SavingsGoal savingsGoal = new SavingsGoal();
        savingsGoal.setUser(user);
        savingsGoal.setName(request.getName());
        savingsGoal.setTargetAmount(request.getTargetAmount());
        savingsGoal.setCurrentAmount(request.getCurrentAmount());
        savingsGoal.setDeadline(request.getDeadline());

        // Check if already completed
        if (request.getCurrentAmount().compareTo(request.getTargetAmount()) >= 0) {
            savingsGoal.setCompleted(true);
        }

        // Save to database
        SavingsGoal savedGoal = savingsGoalRepository.save(savingsGoal);

        // Calculate progress
        int progress = calculateProgress(savedGoal.getCurrentAmount(), savedGoal.getTargetAmount());
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), savedGoal.getDeadline());

        // Return response
        return new SavingsGoalResponse(
                savedGoal.getId(),
                savedGoal.getName(),
                savedGoal.getTargetAmount(),
                savedGoal.getCurrentAmount(),
                savedGoal.getDeadline(),
                progress,
                savedGoal.getCompleted(),
                daysRemaining
        );
    }

    //Get all saving gaols
    public List<SavingsGoalResponse> getAllSavingsGoals(Long userId) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUserIdOrderByDeadlineAsc(userId);
        List<SavingsGoalResponse> responseList = new ArrayList<>();

        for(SavingsGoal goal : goals) {
            int progress = calculateProgress(goal.getCurrentAmount(), goal.getTargetAmount());
            long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), goal.getDeadline());

            SavingsGoalResponse response = new SavingsGoalResponse(
                    goal.getId(),
                    goal.getName(),
                    goal.getTargetAmount(),
                    goal.getCurrentAmount(),
                    goal.getDeadline(),
                    progress,
                    goal.getCompleted(),
                    daysRemaining
            );
            responseList.add(response);
        }

        return responseList;
    }

    // Update savings goal amount
    public SavingsGoalResponse updateSavingsAmount(Long userId, Long goalId, BigDecimal amount) {

        // Find goal
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Savings goal not found"));

        // Verify goal belongs to user
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Savings goal does not belong to user");
        }

        // Update amount
        goal.setCurrentAmount(amount);

        // Check if completed
        if (amount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setCompleted(true);
        }

        // Save
        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);

        // Calculate progress
        int progress = calculateProgress(updatedGoal.getCurrentAmount(), updatedGoal.getTargetAmount());
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), updatedGoal.getDeadline());

        return new SavingsGoalResponse(
                updatedGoal.getId(),
                updatedGoal.getName(),
                updatedGoal.getTargetAmount(),
                updatedGoal.getCurrentAmount(),
                updatedGoal.getDeadline(),
                progress,
                updatedGoal.getCompleted(),
                daysRemaining
        );
    }

    // Calculate progress percentage
    private int calculateProgress(BigDecimal current, BigDecimal target) {
        if (target.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }

        BigDecimal percentage = current
                .multiply(BigDecimal.valueOf(100))
                .divide(target, 0, RoundingMode.HALF_UP);

        return Math.min(percentage.intValue(), 100);
    }
}
