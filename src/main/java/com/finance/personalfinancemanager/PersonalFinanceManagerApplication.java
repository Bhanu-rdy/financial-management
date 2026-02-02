package com.finance.personalfinancemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

@SpringBootApplication
public class PersonalFinanceManagerApplication {

    public static void main(String[] args) {

        SpringApplication.run(PersonalFinanceManagerApplication.class, args);

        System.out.println("Date and Time is : " + LocalDateTime.now());
    }

}
