package com.example.holidayplanner.config.jwt.token;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenConfig {

    @Bean
    CommandLineRunner refreshTokenRepoCommandLineRunner(TokenRepository tokenRepository) {
        return args -> {
            tokenRepository.findAll();
        };
    }
}
