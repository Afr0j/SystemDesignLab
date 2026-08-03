package com.systemdesignlab.urlshortener.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.exception.RedisUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class RateLimiterService {

    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimiterService(
            RedisTemplate<String, String> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    @CircuitBreaker(
            name = "redisRateLimiter",
            fallbackMethod = "redisUnavailable")
    public boolean allowRequest(String ipAddress) {

        String key = "rate_limit:" + ipAddress;

        Long count = redisTemplate
                .opsForValue()
                .increment(key);

        if (count == 1) {
            redisTemplate.expire(key, WINDOW);
        }

        return count <= MAX_REQUESTS;
    }

    public boolean redisUnavailable(
            String ipAddress,
            Throwable ex) {

        throw new RedisUnavailableException(ex);
    }
}