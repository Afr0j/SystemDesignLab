package com.systemdesignlab.urlshortener.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.systemdesignlab.urlshortener.exception.RedisUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class RateLimiterService {

	private static final int MAX_REQUESTS = 5;
	private static final Duration WINDOW = Duration.ofMinutes(1);

	private final RedisTemplate<String, String> redisTemplate;

	private final Counter allowedCounter;
	private final Counter blockedCounter;
	private final Counter redisFailureCounter;

	public RateLimiterService(RedisTemplate<String, String> redisTemplate, MeterRegistry meterRegistry) {

		this.redisTemplate = redisTemplate;
		this.allowedCounter = meterRegistry.counter("rate_limit.allowed");

		this.blockedCounter = meterRegistry.counter("rate_limit.blocked");

		this.redisFailureCounter = meterRegistry.counter("rate_limit.redis.failure");
	}

	@CircuitBreaker(name = "redisRateLimiter", fallbackMethod = "redisUnavailable")
	public boolean allowRequest(String ipAddress) {

		String key = "rate_limit:" + ipAddress;

		Long count = redisTemplate.opsForValue().increment(key);

		if (count == 1) {
			redisTemplate.expire(key, WINDOW);
		}

		boolean allowed = count <= MAX_REQUESTS;

		if (allowed) {

			allowedCounter.increment();

		} else {

			blockedCounter.increment();

		}

		return allowed;
	}

	public boolean redisUnavailable(String ipAddress, Throwable ex) {
		redisFailureCounter.increment();


		throw new RedisUnavailableException(ex);
	}
}