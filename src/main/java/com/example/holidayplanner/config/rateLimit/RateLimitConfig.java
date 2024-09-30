package com.example.holidayplanner.config.rateLimit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Bean
    public IpRateLimiter ipRateLimiter() { return new IpRateLimiter(); }
}
