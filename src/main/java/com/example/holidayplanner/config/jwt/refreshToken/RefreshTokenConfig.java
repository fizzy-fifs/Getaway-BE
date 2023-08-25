package com.example.holidayplanner.config.jwt.refreshToken;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RefreshTokenConfig {

    @Bean
    CommandLineRunner commandLineRunner(RefreshTokenRepository refreshTokenRepository) {
        return args -> {
            refreshTokenRepository.findAll();
        };
    }
}
