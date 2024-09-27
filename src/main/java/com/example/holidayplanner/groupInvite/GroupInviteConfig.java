package com.example.holidayplanner.groupInvite;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

public class GroupInviteConfig {
    @Bean
    CommandLineRunner commandLineRunner(GroupInviteRepository repository) {
        return args -> { repository.findAll(); };
    }
}
