package com.example.holidayplanner.config.jwt.accessToken;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessTokenConfig {

    @Bean
    CommandLineRunner commandLineRunner(AccessTokenRepository accessTokenRepository) {
        return args -> {
            accessTokenRepository.findAll();
        };
    }
}
