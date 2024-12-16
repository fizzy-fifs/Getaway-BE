package com.example.holidayplanner.config;

import com.example.holidayplanner.models.user.UserLookupModel;
import com.example.holidayplanner.repositories.AvailableDatesRepository;
import com.example.holidayplanner.repositories.BudgetRepository;
import com.example.holidayplanner.repositories.GroupInviteRepository;
import com.example.holidayplanner.repositories.UserRepository;
import com.example.holidayplanner.repositories.group.GroupRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    CommandLineRunner availableDatesRepoCommandLineRunner(AvailableDatesRepository repository) {
        return args -> { repository.findAll(); };
    }

    @Bean
    CommandLineRunner budgetRepoCommandLineRunner(BudgetRepository repository) {
        return args -> { repository.findAll(); };
    }

    @Bean
    CommandLineRunner groupRepoCommandLineRunner(GroupRepository repository) {
        return args -> { repository.findAll(); };
    }

    @Bean
    CommandLineRunner groupInviteRepoCommandLineRunner(GroupInviteRepository repository) {
        return args -> { repository.findAll(); };
    }

    @Bean
    CommandLineRunner userRepoCommandLineRunner(UserRepository repository) {
        return args -> { repository.findAll(); };
    }

    @Bean
    UserLookupModel userLookupModel() {
        return new UserLookupModel();
    }
}
