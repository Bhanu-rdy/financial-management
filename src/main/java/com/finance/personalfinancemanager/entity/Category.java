package com.finance.personalfinancemanager.entity;

import com.finance.personalfinancemanager.enums.CategoryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;   // Foreign key references to users table id

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryType type;  //INCOME OR EXPENSE

    @Column(nullable = false, updatable = false, columnDefinition = "datetime")
    private LocalDateTime createdAt = LocalDateTime.now();

}
