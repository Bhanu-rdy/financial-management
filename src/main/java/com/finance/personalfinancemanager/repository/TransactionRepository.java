package com.finance.personalfinancemanager.repository;

import com.finance.personalfinancemanager.entity.Transaction;
import com.finance.personalfinancemanager.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    // Get all transaction for a user
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);

    //Get Transactions by type
    List<Transaction> findByUserIdAndTypeOrderByTransactionDateDesc(Long userId, CategoryType type);

    //Get transactions by date range
    @Query("SELECT t FROM Transaction t WHERE t.user.id = ?1 AND t.transactionDate BETWEEN ?2 AND ?3 ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndDateRange(Long userId, LocalDate startDate, LocalDate endDate);

    //Get transactions by category
    List<Transaction> findByCategoryIdOrderByTransactionDateDesc(Long categoryId);
}

