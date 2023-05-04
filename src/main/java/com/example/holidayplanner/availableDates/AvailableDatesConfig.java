package com.example.holidayplanner.availableDates;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

public class AvailableDatesConfig {

    @Bean
    CommandLineRunner commandLineRunner(AvailableDatesRepository repository) {
        return args -> { repository.findAll(); };
    }
}
