package com.example.holidayplanner.budget;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

public class BudgetConfig {

    @Bean
    CommandLineRunner commandLineRunner(BudgetRepository repository) {
        return args -> { repository.findAll(); };
    }
}
