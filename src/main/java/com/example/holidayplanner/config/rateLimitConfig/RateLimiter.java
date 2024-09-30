package com.example.holidayplanner.config.rateLimitConfig;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import java.time.Duration;

public class RateLimiter {

    public static Bucket getBucket(int tokenLimit, Duration duration) {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(tokenLimit, Refill.greedy(tokenLimit, duration)))
                .build();
    }
}
