package com.finance.personalfinancemanager;

import com.finance.personalfinancemanager.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private String type;   //INCOME OR EXPENSE

}
