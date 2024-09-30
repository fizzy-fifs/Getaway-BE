package com.example.holidayplanner.config.rateLimitConfig;

import io.github.bucket4j.Bucket;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IpRateLimiter {

    private final int TOKEN_LIMIT = 5;
    private final Duration ONE_SECOND_DURATION = Duration.ofSeconds(1);
    private final Map<String, Bucket> inMemoryCache = new ConcurrentHashMap<String, Bucket>();


    public Bucket resolveBucket(String ipAddress) {
        return inMemoryCache.computeIfAbsent(ipAddress, k -> RateLimiter.getBucket(TOKEN_LIMIT, ONE_SECOND_DURATION));
    }
}
