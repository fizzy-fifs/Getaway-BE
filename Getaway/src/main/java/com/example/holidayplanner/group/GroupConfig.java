package com.example.holidayplanner.group;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupConfig {

    @Bean
    CommandLineRunner groupRepoCommandLineRunner(GroupRepository repository) {
        return args -> { repository.findAll(); };
    }
}
